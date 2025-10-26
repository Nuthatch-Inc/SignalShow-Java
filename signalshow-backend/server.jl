# SignalShow Backend Server
# Minimal Julia HTTP server for SignalShow signal processing

using HTTP
using JSON3
using Sockets
using Dates

# Server configuration
const DEFAULT_PORT = 8080
const INACTIVITY_TIMEOUT = 15 * 60 # 15 minutes in seconds

# Track last activity time
last_activity = Ref(time())

"""
Parse command line arguments
"""
function parse_args()
    port = DEFAULT_PORT
    
    for i in 1:length(ARGS)
        if ARGS[i] == "--port" && i < length(ARGS)
            port = parse(Int, ARGS[i + 1])
        end
    end
    
    return port
end

"""
Update last activity timestamp
"""
function update_activity()
    last_activity[] = time()
end

"""
Check if server should shutdown due to inactivity
"""
function check_inactivity()
    if time() - last_activity[] > INACTIVITY_TIMEOUT
        println("\n⏰ Server inactive for $(INACTIVITY_TIMEOUT ÷ 60) minutes, shutting down...")
        exit(0)
    end
end

"""
Health check endpoint
Returns server status and version
"""
function handle_health(req::HTTP.Request)
    update_activity()  # Update activity on health check
    
    response = Dict(
        "status" => "ok",
        "version" => "1.0.0",
        "timestamp" => time(),
        "last_activity" => last_activity[]
    )
    
    return HTTP.Response(200, JSON3.write(response))
end

"""
CORS middleware - allows requests from any origin
"""
function cors_middleware(handler)
    return function(req::HTTP.Request)
        # Set CORS headers
        headers = [
            "Access-Control-Allow-Origin" => "*",
            "Access-Control-Allow-Methods" => "GET, POST, OPTIONS",
            "Access-Control-Allow-Headers" => "Content-Type",
            "Content-Type" => "application/json"
        ]
        
        # Handle preflight OPTIONS request
        if req.method == "OPTIONS"
            return HTTP.Response(200, headers)
        end
        
        # Process the actual request
        response = handler(req)
        
        # Add CORS headers to response
        for (key, value) in headers
            HTTP.setheader(response, key => value)
        end
        
        return response
    end
end

"""
Main request router
"""
function handle_request(req::HTTP.Request)
    # Health check endpoint
    if req.target == "/health"
        return handle_health(req)
    end
    
    # API version endpoint
    if req.target == "/api/version"
        return handle_health(req)  # Same response for now
    end
    
    # TODO: Add more endpoints:
    # - /api/functions/generate - Generate signal functions
    # - /api/operations/fft - Perform FFT
    # - /api/operations/filter - Apply filters
    # etc.
    
    # 404 Not Found
    error_response = Dict(
        "error" => "Not Found",
        "message" => "Endpoint $(req.target) does not exist"
    )
    return HTTP.Response(404, JSON3.write(error_response))
end

"""
Start the HTTP server
"""
function start_server(port::Int)
    println("🚀 Starting SignalShow backend server...")
    println("📡 Port: $port")
    println("🔗 Health check: http://localhost:$port/health")
    println("⏱️  Started at: $(Dates.now())")
    println("⏰ Auto-shutdown after $(INACTIVITY_TIMEOUT ÷ 60) minutes of inactivity")
    println()
    println("Press Ctrl+C to stop the server")
    println("=" ^ 50)
    
    # Wrap handler with CORS middleware
    handler = cors_middleware(handle_request)
    
    # Start inactivity checker in background
    @async begin
        while true
            sleep(60)  # Check every minute
            check_inactivity()
        end
    end
    
    try
        # Start HTTP server
        HTTP.serve(handler, Sockets.localhost, port)
    catch e
        if isa(e, InterruptException)
            println("\n")
            println("=" ^ 50)
            println("🛑 Server stopped gracefully")
        else
            println("\n")
            println("=" ^ 50)
            println("❌ Server error: ", e)
            rethrow(e)
        end
    end
end

# Main entry point
function main()
    port = parse_args()
    
    println()
    println("╔════════════════════════════════════════╗")
    println("║     SignalShow Backend Server         ║")
    println("║     Signal Processing with Julia      ║")
    println("╚════════════════════════════════════════╝")
    println()
    
    start_server(port)
end

# Run the server
if abspath(PROGRAM_FILE) == @__FILE__
    main()
end
