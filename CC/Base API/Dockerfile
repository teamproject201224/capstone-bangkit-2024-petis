# Dockerfile
FROM node:18-slim
WORKDIR /usr/src/app
RUN apt-get update \
    && apt-get install -y wget gnupg ca-certificates \
    && apt-get install -y libnss3 libxss1 libasound2 libatk1.0-0 \
    libcups2 libdbus-1-3 libxcomposite1 libxrandr2 libxdamage1 \
    libxfixes3 libx11-xcb1 libxcursor1 libxi6 libxinerama1 libpangocairo-1.0-0 \
    libpangoft2-1.0-0 fonts-liberation libfontconfig1 \
    libatk-bridge2.0-0 libgbm-dev libgtk-3-0 \
    libxshmfence-dev \
    && apt-get install -y --no-install-recommends \
    gconf-service libappindicator1 libgbm-dev libxshmfence-dev \
    && apt-get clean && rm -rf /var/lib/apt/lists/*
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 8080
CMD ["node", "server.js"]
