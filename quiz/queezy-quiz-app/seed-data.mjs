import mysql from 'mysql2/promise';
import dotenv from 'dotenv';

dotenv.config();

const connection = await mysql.createConnection(process.env.DATABASE_URL);

// Clear existing data
await connection.query('DELETE FROM quizzes');
await connection.query('DELETE FROM categories');

// Insert categories
const categories = [
  { name: '수학', description: '기초 수학부터 고급 수학까지', icon: '🔢', color: '#8b5cf6' },
  { name: '과학', description: '물리, 화학, 생물학', icon: '🔬', color: '#06b6d4' },
  { name: '역사', description: '세계 역사와 문화', icon: '📜', color: '#f59e0b' },
];

const categoryResults = [];
for (const cat of categories) {
  const [result] = await connection.query(
    'INSERT INTO categories (name, description, icon, color, `order`) VALUES (?, ?, ?, ?, ?)',
    [cat.name, cat.description, cat.icon, cat.color, categoryResults.length]
  );
  categoryResults.push(result.insertId);
}

// Insert quizzes for each category
const mathQuizzes = [
  {
    question: '2 + 2 = ?',
    options: ['2', '3', '4', '5'],
    correctAnswer: 2,
    explanation: '2 더하기 2는 4입니다.',
    difficulty: 'easy',
  },
  {
    question: '15 × 3 = ?',
    options: ['35', '45', '55', '65'],
    correctAnswer: 1,
    explanation: '15 곱하기 3은 45입니다.',
    difficulty: 'easy',
  },
  {
    question: '100 ÷ 5 = ?',
    options: ['15', '20', '25', '30'],
    correctAnswer: 1,
    explanation: '100을 5로 나누면 20입니다.',
    difficulty: 'medium',
  },
];

const scienceQuizzes = [
  {
    question: '물의 끓는점은 몇 도인가요?',
    options: ['50°C', '75°C', '100°C', '125°C'],
    correctAnswer: 2,
    explanation: '표준 대기압에서 물의 끓는점은 100°C입니다.',
    difficulty: 'easy',
  },
  {
    question: '태양계의 행성은 몇 개인가요?',
    options: ['7개', '8개', '9개', '10개'],
    correctAnswer: 1,
    explanation: '태양계에는 8개의 행성이 있습니다.',
    difficulty: 'easy',
  },
  {
    question: '인간의 DNA는 몇 개의 염기쌍으로 이루어져 있나요?',
    options: ['약 30억 개', '약 50억 개', '약 70억 개', '약 100억 개'],
    correctAnswer: 0,
    explanation: '인간의 DNA는 약 30억 개의 염기쌍으로 이루어져 있습니다.',
    difficulty: 'hard',
  },
];

const historyQuizzes = [
  {
    question: '한국 전쟁은 언제 일어났나요?',
    options: ['1945년', '1950년', '1960년', '1970년'],
    correctAnswer: 1,
    explanation: '한국 전쟁은 1950년 6월 25일에 시작되었습니다.',
    difficulty: 'medium',
  },
  {
    question: '프랑스 혁명은 언제 일어났나요?',
    options: ['1789년', '1799년', '1809년', '1819년'],
    correctAnswer: 0,
    explanation: '프랑스 혁명은 1789년에 시작되었습니다.',
    difficulty: 'medium',
  },
  {
    question: '세계 2차 대전은 언제 끝났나요?',
    options: ['1943년', '1944년', '1945년', '1946년'],
    correctAnswer: 2,
    explanation: '제2차 세계 대전은 1945년 9월에 공식적으로 종료되었습니다.',
    difficulty: 'easy',
  },
];

const quizzesByCategory = [
  { categoryId: categoryResults[0], quizzes: mathQuizzes },
  { categoryId: categoryResults[1], quizzes: scienceQuizzes },
  { categoryId: categoryResults[2], quizzes: historyQuizzes },
];

for (const { categoryId, quizzes } of quizzesByCategory) {
  for (let i = 0; i < quizzes.length; i++) {
    const quiz = quizzes[i];
    await connection.query(
      'INSERT INTO quizzes (categoryId, question, options, correctAnswer, explanation, difficulty, `order`) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [
        categoryId,
        quiz.question,
        JSON.stringify(quiz.options),
        quiz.correctAnswer,
        quiz.explanation,
        quiz.difficulty,
        i,
      ]
    );
  }
}

console.log('✅ 샘플 데이터가 성공적으로 생성되었습니다!');
console.log(`- 카테고리: ${categories.length}개`);
console.log(`- 퀴즈: ${mathQuizzes.length + scienceQuizzes.length + historyQuizzes.length}개`);

await connection.end();
