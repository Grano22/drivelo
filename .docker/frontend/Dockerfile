FROM node:20-alpine AS builder

ENV NG_CLI_ANALYTICS=false
ENV CI=true

WORKDIR /app

COPY ./frontend/package.json ./frontend/package-lock.json ./

RUN npm ci --no-audit --no-fund

COPY ./frontend/ .

ARG BUILD_CONFIGURATION=production
ARG APP_NAME=app

RUN npx ng build --configuration "${BUILD_CONFIGURATION}"

FROM nginx:1.27-alpine AS runtime

COPY ./.docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

ARG APP_NAME=app
COPY --from=builder /app/dist/${APP_NAME}/ /usr/share/nginx/html/

USER nginx

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -q -O /dev/null http://127.0.0.1:8080/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
