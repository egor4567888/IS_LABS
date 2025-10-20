/** @type {import('tailwindcss').Config} */
export default {
    content: [
      "./index.html",
      "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            backdropBlur: {
              '0': '0',
            },
            backgroundOpacity: {
              '100': '1',
            }
          },
    },
    plugins: [],
  }