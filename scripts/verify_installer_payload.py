#!/usr/bin/env python3
"""Check the application payload extracted from a finished native installer."""
import argparse
import hashlib
from pathlib import Path
import re
import xml.etree.ElementTree as ET
import zipfile


def verify(payload, reference):
    root = Path(__file__).resolve().parents[1]
    version = ET.parse(root / 'pom.xml').getroot().find('{*}version').text
    jar_name = f'signalshow-{version}.jar'
    expected = {jar_name, 'SignalShow.cfg'}
    actual = {p.name for p in payload.iterdir()}
    if not expected <= actual or actual - expected - {'.jpackage.xml', '.package'}:
        raise ValueError(f'Unexpected installer app files: {sorted(actual)}')
    if any(not p.is_file() for p in payload.iterdir()):
        raise ValueError('Application payload must not include build directories')
    jar = payload / jar_name
    if jar.read_bytes() != reference.read_bytes():
        raise ValueError('Installed JAR differs from the executable built in this job')
    with zipfile.ZipFile(jar) as archive:
        names = set(archive.namelist())
        if 'Main-Class: SignalShow' not in archive.read('META-INF/MANIFEST.MF').decode():
            raise ValueError('Executable entry point is missing')
        if 'javax/media/jai/PlanarImage.class' not in names:
            raise ValueError('JAI dependency is missing')
        forbidden = ('org/junit/', 'org/opentest4j/', 'org/apiguardian/',
                     'test-classes/', 'surefire-reports/', 'src/test/')
        if any(n.startswith(forbidden) or n.endswith('.jar') for n in names):
            raise ValueError('Executable includes tests, reports or nested JAR builds')
        for source in (root / 'src/test/java').rglob('*.java'):
            package = re.search(r'^package\s+([\w.]+)\s*;', source.read_text(encoding='utf-8'), re.M)
            prefix = (package.group(1).replace('.', '/') + '/' if package else '') + source.stem
            if any(n == prefix + '.class' or n.startswith(prefix + '$') for n in names):
                raise ValueError(f'Executable includes test class {prefix}')
    print(f'Installer app payload verified: {sorted(actual)}')
    print(f'Executable SHA256: {hashlib.sha256(jar.read_bytes()).hexdigest()}')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('payload', type=Path)
    parser.add_argument('--jar', type=Path, required=True)
    args = parser.parse_args()
    verify(args.payload, args.jar)
