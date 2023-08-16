/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./client/**/*.{html,css,clj,cljs}",
    "./node_modules/react-tailwindcss-datepicker/dist/index.esm.js",
  ],
  darkMode: "class",
  theme: {
    extend: {},
  },
  plugins: [require("@tailwindcss/forms")],
};
