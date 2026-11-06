const { yaraScan } = require('./yara-scan.js');

const target = process.argv[2];

if (!target) {
  console.error('Uso: node main.js <arquivo>');
  process.exit(1);
}

const result = yaraScan(target);
console.log(JSON.stringify(result, null, 2));
