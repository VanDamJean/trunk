import { useEffect, useRef } from "react";

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  life: number;
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
  const onCompleteRef = useRef(onComplete);
  onCompleteRef.current = onComplete;

  useEffect(() => {
    if (!isActive) return;

    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    const colors = [
      "#FFD700",
      "#FF6B6B",
      "#4ECDC4",
      "#45B7D1",
      "#FFA07A",
      "#98D8C8",
      "#F7DC6F",
      "#BB8FCE",
    ];

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
          color: colors[Math.floor(Math.random() * colors.length)]!,
          size: 4 + Math.random() * 6,
        });
      }
    };

    const animate = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      const particles = particlesRef.current;

      for (let i = particles.length - 1; i >= 0; i--) {
        const p = particles[i]!;
        p.vy += 0.2;
        p.vx *= 0.98;
        p.vy *= 0.98;
        p.x += p.vx;
        p.y += p.vy;
        p.life -= 0.015;

        if (p.life <= 0) {
          particles.splice(i, 1);
          continue;
        }

        ctx.save();
        ctx.globalAlpha = p.life;
        ctx.fillStyle = p.color;
        ctx.translate(p.x, p.y);
        ctx.rotate((1 - p.life) * Math.PI * 2);
        ctx.fillRect(-p.size / 2, -p.size / 2, p.size, p.size);
        ctx.restore();
      }

      if (particles.length === 0) {
        if (animationIdRef.current) cancelAnimationFrame(animationIdRef.current);
        onCompleteRef.current?.();
        return;
      }

      animationIdRef.current = requestAnimationFrame(animate);
    };

    createConfetti();
    animate();

    const secondBurstTimeout = setTimeout(() => {
      createConfetti();
    }, 300);

    return () => {
      clearTimeout(secondBurstTimeout);
      if (animationIdRef.current) cancelAnimationFrame(animationIdRef.current);
      particlesRef.current = [];
    };
  }, [isActive]);

  if (!isActive) return null;

  return (
    <canvas
      ref={canvasRef}
      className="pointer-events-none fixed inset-0 z-40"
      style={{ background: "transparent" }}
    />
  );
}
