const cp = require('child_process');
const path = require('path');

function yaraScan(filePathResolved) {
    const startedAt = process.hrtime.bigint();

    try {
        const absolutePath = path.resolve(filePathResolved);
        const filename = path.basename(absolutePath);
        const extension = path.extname(absolutePath).replace('.', '');

        const args = [
            '-d', `filepath=${absolutePath}`,
            '-d', `filename=${filename}`,
            '-d', `extension=${extension}`,
            'yara_rules/index.yar',
            absolutePath,
        ];

        const yaraResult = cp.spawnSync('yara', args, { encoding: 'utf-8' });
        const elapsedMs = Number((process.hrtime.bigint() - startedAt) / BigInt(1_000_000));

        if (yaraResult.status === 0) {
            const matches = yaraResult.stdout.split('\n').filter(line => line.trim() !== '');
            return {
                status: 'scanned',
                matches,
                error: null,
                elapsed_ms: elapsedMs,
            };
        }

        return {
            status: 'error',
            matches: null,
            error: yaraResult.stderr ? yaraResult.stderr.trim() : 'Unknown error',
            elapsed_ms: elapsedMs,
        };
    } catch (e) {
        const elapsedMs = Number((process.hrtime.bigint() - startedAt) / BigInt(1_000_000));
        console.error(`Error executing yara scan: ${e.message}`);
        console.error(`Elapsed time: ${elapsedMs} ms`);
        throw e;
    }
}

if (require.main === module) {
    const target = process.argv[2];
    if (!target) {
        console.error('Uso: node yara-scan.js <arquivo>');
        process.exit(1);
    }

    const result = yaraScan(target);
    console.log(JSON.stringify(result, null, 2));
}

module.exports = { yaraScan };