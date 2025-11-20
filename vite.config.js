import { defineConfig } from 'vite';

export default defineConfig({
  test: {
    include: ["pub/**/**test.mjs", "pub/**/**test.jsx"],
  },
});

