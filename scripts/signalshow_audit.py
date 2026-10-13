#!/usr/bin/env python3
"""Validate SignalShow's audit inventory and generate its read-only report."""
import argparse
from collections import Counter
import html
import json
from pathlib import Path
import re
from urllib.parse import quote

ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / 'docs/audit/signalshow-coverage.json'
REPORT = ROOT / 'docs/audit/index.html'
CHECKS = ('definition', 'equations', 'numerics', 'parameters', 'handbook')
STATUSES = {'unreviewed', 'partial', 'verified', 'issue', 'not-applicable'}
SCENARIO_STATUSES = {'checked', 'pending', 'issue', 'scope', 'deferred'}
LABELS = {'unreviewed': 'Unreviewed', 'partial': 'Partial', 'verified': 'Verified',
          'issue': 'Known bug', 'not-applicable': 'N/A', 'checked': 'Checked',
          'pending': 'Needs check', 'scope': 'Needs scoping', 'deferred': 'Deferred'}


def inventory():
    source = (ROOT / 'src/main/java/signals/core/DataGeneratorCollections.java').read_text()
    source = re.sub(r'/\*.*?\*/|//[^\n]*', '', source, flags=re.S)
    return {f'{group}:{name}' for group, name in
            re.findall(r'(\w+List)\.add\(\s*(\w+)\.class\s*\)', source)}


def validate(data):
    assert data['schemaVersion'] == 1, 'Unsupported schema'
    evidence = data['evidence']
    assert evidence, 'Evidence inventory is required'
    for eid, item in evidence.items():
        for key in ('date', 'summary', 'limits', 'environment'):
            assert item.get(key), f'{eid}: missing {key}'
        for source in item['sources']:
            assert not Path(source).is_absolute() and '..' not in Path(source).parts, source
            assert (ROOT / source).is_file(), f'{eid}: missing source {source}'
    entries = data['math']
    ids = [row['id'] for row in entries]
    assert len(ids) == len(set(ids)), 'Duplicate math IDs'
    assert set(ids) == inventory(), 'Math inventory differs from registered classes; reconcile rows explicitly'
    for row in entries:
        assert set(row['checks']) == set(CHECKS), row['id']
        assert set(row['checks'].values()) <= STATUSES, row['id']
        assert row['scope'] and row['remaining'] and (ROOT / row['source']).is_file(), row['id']
        assert set(row['evidence']) <= evidence.keys(), row['id']
        if any(value in ('verified', 'partial', 'issue') for value in row['checks'].values()):
            assert row['evidence'], f'{row["id"]}: reviewed checks require evidence'
        if 'not-applicable' in row['checks'].values():
            assert row.get('notApplicable'), row['id']
    scenarios = data['scenarios']
    ids = [row['id'] for row in scenarios]
    assert len(ids) == len(set(ids)), 'Duplicate scenario IDs'
    for row in scenarios:
        assert row['status'] in SCENARIO_STATUSES, row['id']
        assert row['area'] in ('usage', 'files', 'platform', 'memory'), row['id']
        assert row['steps'] and row['expected'] and row['scope'], row['id']
        assert set(row['evidence']) <= evidence.keys(), row['id']
        if row['status'] == 'checked':
            assert row['evidence'] and row['environment'] and row['reviewedOn'], row['id']
        if row['status'] == 'deferred':
            assert row.get('reason'), row['id']
    by_id = {row['id']: row for row in scenarios}
    for sid in data['scenarioPlan']['activeIds'] + data['scenarioPlan']['nextIds']:
        assert sid in by_id and by_id[sid]['status'] != 'checked', f'Invalid active/queued scenario: {sid}'
    for row in data['limitations']:
        assert row['basis'] in ('source', 'measurement', 'reported'), row['id']
        assert row['evidence'] and set(row['evidence']) <= evidence.keys(), row['id']
        assert row['impact'] and row['next'], row['id']


def esc(value):
    return html.escape(str(value))


def source_link(source, label=None):
    return f'<a href="../../{quote(source, safe="/")}">{esc(label or source)}</a>'


def evidence_links(ids):
    return ', '.join(f'<a href="#evidence-{esc(eid)}">{esc(eid)}</a>' for eid in ids) or 'None yet'


def badge(status):
    return f'<span class="status {esc(status)}">{esc(LABELS[status])}</span>'


def render(data):
    counts = Counter(row['status'] for row in data['scenarios'])
    out = ['<!doctype html><html lang="en"><meta charset="utf-8">',
           '<meta name="viewport" content="width=device-width,initial-scale=1">',
           '<title>SignalShow Java testing matrix</title>',
           '''<style>
*{box-sizing:border-box}html{scroll-behavior:smooth}body{margin:0;background:#f6f7f9;color:#202b36;font:15px/1.55 system-ui,sans-serif}main{max-width:1320px;margin:auto;padding:36px 28px 80px}h1{font-size:34px;margin:8px 0}h2{margin-top:48px;font-size:25px}h3{font-size:18px}p{max-width:95ch}a{color:#195c94;text-underline-offset:3px}nav{display:flex;flex-wrap:wrap;gap:18px;margin:24px 0}table{width:100%;border-collapse:collapse;background:white}th,td{padding:11px 13px;text-align:left;border-bottom:1px solid #dbe1e7;vertical-align:top}th{background:#eaf0f5;font-size:13px}td p{margin:5px 0}td ul,td ol{padding-left:20px;margin:6px 0}.table-wrap{overflow:auto}.status{display:inline-block;white-space:nowrap;border-radius:4px;padding:2px 7px;font-size:12px;background:#edf0f4;color:#4b5663}.verified,.checked{background:#dfefe6;color:#175435}.partial,.scope{background:#fff0cd;color:#795307}.issue{background:#f8dfdf;color:#8a2020}.muted,small{color:#5b6673}.callout{background:#fff;border-left:4px solid #2c739f;padding:16px 22px;margin:22px 0}.math td:first-child{min-width:225px}.math .detail{min-width:340px}section,article{scroll-margin-top:20px}.evidence{border-top:1px solid #dbe1e7;padding:12px 0}code{font-size:13px;overflow-wrap:anywhere}li{margin:4px 0}@media(prefers-color-scheme:dark){body{background:#151b22;color:#e4eaf0}table,.callout{background:#1d2630}th{background:#253241}td,th,.evidence{border-color:#344352}a{color:#8dc4f5}.muted,small{color:#acbac7}.status{background:#344352;color:#d1dce7}.verified,.checked{background:#1f4935;color:#bcdfca}.partial,.scope{background:#534322;color:#f3d990}.issue{background:#593133;color:#ffcdcd}}@media(max-width:650px){main{padding:20px 14px}h1{font-size:28px}}@media print{body{background:white;color:black}main{max-width:none;padding:0}nav{display:none}.table-wrap{overflow:visible}.status{border:1px solid #ccc}a{color:inherit}tr{break-inside:avoid}}
</style><main>''',
           '<p class="muted">SignalShow Java · development audit</p>',
           '<h1>Testing matrix</h1>',
           f'<p>{esc(data["purpose"])}</p>',
           f'<p class="muted">Updated {esc(data["reviewedOn"])} · baseline <code>{esc(data["baselineCommit"][:10])}</code> plus local changes</p>',
           '<nav aria-label="Report sections"><a href="#queue">Current work</a><a href="#usage">Usage</a><a href="#files">Files</a><a href="#math">Mathematics</a><a href="#memory">Memory</a><a href="#platform">Platforms</a><a href="#human">Human testing</a><a href="#evidence">Evidence</a></nav>',
           f'<div class="callout">{len(data["math"])} registered mathematical entries; {len(data["scenarios"])} bounded scenarios, {counts["checked"]} checked. These counts describe recorded evidence, not whole-application completion. Headless tests do not certify visible desktop behavior or Windows.</div>',
           '<section id="queue"><h2>Current work</h2>',
           f'<p>{esc(data["scenarioPlan"]["focus"])}</p>',
           f'<p>Continuous audit: <strong>{esc(data["activity"]["state"])}</strong>. Usage monitoring: <code>check_usage: {str(data["activity"]["check_usage"]).lower()}</code>.</p>']
    by_id = {row['id']: row for row in data['scenarios']}
    for title, key in [('Active', 'activeIds'), ('Next', 'nextIds')]:
        out.append(f'<p><strong>{title}:</strong> ' + (', '.join(
            f'<a href="#{esc(sid)}">{esc(by_id[sid]["title"])}</a>' for sid in data['scenarioPlan'][key]) or 'None') + '</p>')
    out.append('</section><p>Status: Checked applies only to the written scope; Needs check is a concrete missing test; Needs scoping requires a decision; Known bug has evidence; Deferred states why.</p>')
    for area, title in [('usage', 'Actual usage and workflow checks'), ('files', 'Files and persistence'),
                        ('memory', 'Memory and capacity'), ('platform', 'Builds and platforms')]:
        out.append(f'<section id="{area}"><h2>{title}</h2>')
        if area == 'memory':
            for limit in data['limitations']:
                out.append(f'<article><h3>{esc(limit["title"])}</h3><p>{esc(limit["impact"])}</p><p><strong>Basis:</strong> {esc(limit["basis"])}. {esc(limit["scope"])}</p><p><strong>Next:</strong> {esc(limit["next"])}</p><p>{evidence_links(limit["evidence"])}</p></article>')
            out.append('<p>Payload estimate only: each channel is width × height × 8 bytes. This excludes array headers, original source arrays, operation intermediates, images and the JVM.</p><table><thead><tr><th>Square image</th><th>One double channel</th><th>Real + imaginary</th><th>Four double channels</th></tr></thead><tbody>')
            for size in (256, 512, 1024, 2048, 4096):
                mib = size * size * 8 / 1048576
                out.append(f'<tr><td>{size} × {size}</td><td>{mib:g} MiB</td><td>{2*mib:g} MiB</td><td>{4*mib:g} MiB</td></tr>')
            out.append('</tbody></table>')
        out.append('<div class="table-wrap"><table><thead><tr><th>Scenario</th><th>Status / scope</th><th>Procedure and acceptance</th><th>Evidence</th></tr></thead><tbody>')
        for row in (r for r in data['scenarios'] if r['area'] == area):
            steps = ''.join(f'<li>{esc(step)}</li>' for step in row['steps'])
            out.append(f'<tr id="{esc(row["id"])}"><td><strong>{esc(row["title"])}</strong><p><small>{esc(row["id"])} · {esc(row["priority"])}</small></p></td><td>{badge(row["status"])}<p>{esc(row["summary"])}</p><p><small>{esc(row["scope"])}</small></p></td><td><ol>{steps}</ol><p><strong>Expected:</strong> {esc(row["expected"])}</p></td><td>{evidence_links(row["evidence"])}<p><small>{esc(row["environment"])}</small></p></td></tr>')
        out.append('</tbody></table></div></section>')
    out.append('<section id="math"><h2>Mathematical accuracy</h2><p>The registry inventory includes each 1D/2D function or operation separately. Definition, generated equations, numerical results, parameters and handbook explanations are independent checks. Specialized menus and demonstrations also require usage coverage.</p><div class="table-wrap"><table class="math"><thead><tr><th>Function / operation</th>')
    out.extend(f'<th>{esc(check.title())}</th>' for check in CHECKS)
    out.append('<th>Verification scope and remaining work</th></tr></thead><tbody>')
    for row in data['math']:
        out.append(f'<tr id="{esc(row["id"])}"><td><strong>{esc(row["title"])}</strong><p><small>{esc(row["area"])}</small></p>{source_link(row["source"], row["class"])}</td>')
        out.extend(f'<td>{badge(row["checks"][check])}</td>' for check in CHECKS)
        out.append(f'<td class="detail"><p>{esc(row["scope"])}</p><p><strong>Remaining:</strong> {esc(row["remaining"])}</p>{evidence_links(row["evidence"])}</td></tr>')
    out.append('</tbody></table></div></section><section id="human"><h2>Human testing</h2>')
    for row in data['humanTesting']:
        out.append(f'<p><strong>{esc(row["tester"])}:</strong> {esc(row["status"])}. {esc(row["scope"])}</p>')
    out.append('</section><section id="evidence"><h2>Evidence and limits</h2>')
    for eid, item in data['evidence'].items():
        links = '<br>'.join(source_link(source) for source in item['sources'])
        out.append(f'<article class="evidence" id="evidence-{esc(eid)}"><h3>{esc(eid)}</h3><p>{esc(item["date"])} · {esc(item["environment"])}</p><p>{esc(item["summary"])}</p><p><strong>Limits:</strong> {esc(item["limits"])}</p><p>{links}</p></article>')
    out.append('</section><p class="muted">Generated from <a href="signalshow-coverage.json">the authoritative JSON inventory</a>. ' + source_link('JAVA_TEST_PLAN.md', 'Audit procedure and handoff') + '. Regenerate with <code>python3 scripts/signalshow_audit.py</code>.</p></main></html>\n')
    return '\n'.join(out)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true', help='Validate inventory and report freshness without writing')
    args = parser.parse_args()
    data = json.loads(DATA.read_text())
    validate(data)
    report = render(data)
    if args.check:
        assert REPORT.is_file() and REPORT.read_text() == report, 'Report is stale; regenerate it'
    else:
        REPORT.write_text(report)
    counts = dict(Counter(row['status'] for row in data['scenarios']))
    print(f'Audit validated: {len(data["math"])} registered entries; {len(data["scenarios"])} scenarios {counts}')


if __name__ == '__main__':
    main()
