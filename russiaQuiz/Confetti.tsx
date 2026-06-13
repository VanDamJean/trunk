import { useEffect, useRef } from 'react';

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  life: number;
  maxLife: number;
  color: string;
  size: number;
}

interface ConfettiProps {
  isActive: boolean;
  onComplete?: () => void;
}

export default function Confetti({ isActive, onComplete }: ConfettiProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const particlesRef = useRef<Particle[]>([]);
  const animationIdRef = useRef<number | null>(null);

  useEffect(() => {
    if (!isActive) return;

    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    // 캔버스 크기 설정
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    // 폭죽 색상
    const colors = [
      '#FFD700', // 금색
      '#FF6B6B', // 빨강
      '#4ECDC4', // 청록
      '#45B7D1', // 파랑
      '#FFA07A', // 주황
      '#98D8C8', // 민트
      '#F7DC6F', // 노랑
      '#BB8FCE', // 보라
    ];

    // 초기 폭죽 생성
    const createConfetti = () => {
      const centerX = canvas.width / 2;
      const centerY = canvas.height / 2;
      const particleCount = 80;

      for (let i = 0; i < particleCount; i++) {
        const angle = (Math.PI * 2 * i) / particleCount;
        const velocity = 8 + Math.random() * 8;
        const vx = Math.cos(angle) * velocity;
        const vy = Math.sin(angle) * velocity;

        particlesRef.current.push({
          x: centerX,
          y: centerY,
          vx,
          vy,
          life: 1,
          maxLife: 1,
          color: colors[Math.floor(Math.random() * colors.length)],
          size: 4 + Math.random() * 6,
        });
      }
    };

    // 애니메이션 루프
    const animate = () => {
      // 배경 지우기 (투명도 포함)
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      const particles = particlesRef.current;

      for (let i = particles.length - 1; i >= 0; i--) {
        const p = particles[i];

        // 물리 업데이트
        p.vy += 0.2; // 중력
        p.vx *= 0.98; // 공기 저항
        p.vy *= 0.98;

        p.x += p.vx;
        p.y += p.vy;

        // 생명 감소
        p.life -= 0.015;

        if (p.life <= 0) {
          particles.splice(i, 1);
          continue;
        }

        // 입자 그리기
        ctx.save();
        ctx.globalAlpha = p.life;
        ctx.fillStyle = p.color;

        // 회전 효과
        ctx.translate(p.x, p.y);
        ctx.rotate((1 - p.life) * Math.PI * 2);
        ctx.fillRect(-p.size / 2, -p.size / 2, p.size, p.size);
        ctx.restore();
      }

      // 모든 입자가 사라졌으면 애니메이션 종료
      if (particles.length === 0) {
        if (animationIdRef.current) {
          cancelAnimationFrame(animationIdRef.current);
        }
        onComplete?.();
        return;
      }

      animationIdRef.current = requestAnimationFrame(animate);
    };

    // 폭죽 생성 및 애니메이션 시작
    createConfetti();
    animate();

    // 추가 폭죽 효과 (0.3초 후)
    const secondBurstTimeout = setTimeout(() => {
      createConfetti();
    }, 300);

    // 정리
    return () => {
      clearTimeout(secondBurstTimeout);
      if (animationIdRef.current) {
        cancelAnimationFrame(animationIdRef.current);
      }
      particlesRef.current = [];
    };
  }, [isActive, onComplete]);

  if (!isActive) return null;

  return (
    <canvas
      ref={canvasRef}
      className="fixed inset-0 pointer-events-none z-40"
      style={{ background: 'transparent' }}
    />
  );
}
