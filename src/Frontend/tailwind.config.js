/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#0A0A0A',
        paper: '#F5F5F3',
        gray: {
          100: '#EBEBEA',
          200: '#D6D6D4',
          400: '#A0A09E',
          600: '#6B6B69',
          800: '#3A3A38',
        },
        accent: {
          DEFAULT: '#1A1A1A',
          2: '#E8E0D5',
        },
        priority: {
          low: '#A8C5A0',
          medium: '#D4B896',
          high: '#C4876A',
        },
        status: {
          todo: '#A0A09E',
          progress: '#7B9EC4',
          done: '#8BB89A',
        },
      },
      fontFamily: {
        display: ['"Playfair Display"', 'Georgia', 'serif'],
        body: ['Inter', '"Helvetica Neue"', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', '"Fira Code"', 'monospace'],
      },
      fontSize: {
        xs: 'clamp(0.625rem, 0.5vw + 0.5rem, 0.75rem)',
        sm: 'clamp(0.75rem, 0.5vw + 0.6rem, 0.875rem)',
        base: 'clamp(0.875rem, 0.5vw + 0.7rem, 1rem)',
        lg: 'clamp(1rem, 0.5vw + 0.8rem, 1.125rem)',
        xl: 'clamp(1.125rem, 1vw + 0.8rem, 1.5rem)',
        '2xl': 'clamp(1.5rem, 2vw + 0.8rem, 2.25rem)',
        '3xl': 'clamp(2rem, 3vw + 0.8rem, 3.5rem)',
        '4xl': 'clamp(3rem, 5vw + 1rem, 6rem)',
        hero: 'clamp(4rem, 8vw + 1rem, 10rem)',
      },
      borderRadius: {
        sm: '4px',
        md: '8px',
        lg: '16px',
        xl: '24px',
        full: '9999px',
      },
      boxShadow: {
        sm: '0 1px 2px rgba(0,0,0,0.04)',
        md: '0 4px 16px rgba(0,0,0,0.06)',
        lg: '0 8px 32px rgba(0,0,0,0.10)',
        xl: '0 16px 64px rgba(0,0,0,0.14)',
        card: '0 2px 8px rgba(0,0,0,0.06), 0 0 0 1px rgba(0,0,0,0.04)',
      },
      transitionTimingFunction: {
        out: 'cubic-bezier(0.16, 1, 0.3, 1)',
        in: 'cubic-bezier(0.4, 0, 1, 1)',
        inout: 'cubic-bezier(0.76, 0, 0.24, 1)',
        elastic: 'cubic-bezier(0.34, 1.56, 0.64, 1)',
      },
      transitionDuration: {
        fast: '150ms',
        base: '250ms',
        slow: '400ms',
        xslow: '600ms',
        page: '800ms',
      },
      keyframes: {
        shimmer: {
          from: { backgroundPosition: '100% 0' },
          to: { backgroundPosition: '-100% 0' },
        },
        shake: {
          '0%,100%': { transform: 'translateX(0)' },
          '25%,75%': { transform: 'translateX(-4px)' },
          '50%': { transform: 'translateX(4px)' },
        },
      },
      animation: {
        shimmer: 'shimmer 1.4s infinite linear',
        shake: 'shake 300ms ease',
      },
      maxWidth: {
        container: '1440px',
      },
    },
  },
  plugins: [],
}
