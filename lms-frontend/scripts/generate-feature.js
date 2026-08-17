import fs from 'fs';
import path from 'path';

/**
 * Lấy tên feature từ command line
 * Ví dụ: npm run g room → featureName = "room"
 */
const featureName = process.argv[2];

if (!featureName) {
  console.log('Please provide feature name');
  process.exit(1);
}

/**
 * Base path của feature mới
 * src/features/<featureName>
 */
const basePath = path.join(
  process.cwd(),
  'src',
  'features',
  featureName,
);

/**
 * Danh sách folder chuẩn cho mỗi feature
 */
const folders = [
  'api',
  'components',
  'constants',
  'pages',
  'types',
];

/**
 * Check nếu feature đã tồn tại
 */
if (fs.existsSync(basePath)) {
  console.log(`Feature "${featureName}" already exists`);
  process.exit(1);
}

/**
 * Tạo folder root của feature
 * recursive: true → tạo cả parent folder nếu chưa tồn tại
 */
fs.mkdirSync(basePath, {
  recursive: true,
});

/**
 * Tạo các sub-folder bên trong feature
 * folders.forEach → lặp qua từng folder trong danh sách và tạo chúng
 * path.join(basePath, folder) → tạo đường dẫn đầy đủ cho mỗi folder con
 * recursive: true → đảm bảo tạo được folder con dù parent folder đã tồn tại hay chưa
 */
folders.forEach((folder) => {
  fs.mkdirSync(
    path.join(basePath, folder),
    { recursive: true },
  );
});

/**
 * Done
 */
console.log(
  `Feature "${featureName}" created successfully`,
);