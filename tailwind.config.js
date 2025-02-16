/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/cljs/**/*.{html,css,js,jsx,ts,tsx,clj,cljs}",
    "./resources/public/**/*.{html,css,js,jsx,ts,tsx,clj,cljs}",
  ],
  darkMode: "class",
  theme: {
    extend: {},
  },
  plugins: [],
};
