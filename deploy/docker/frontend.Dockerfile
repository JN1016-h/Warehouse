ARG REGISTRY_MIRROR=docker.io/library
FROM ${REGISTRY_MIRROR}/node:18-alpine AS build
WORKDIR /fe

COPY src/main/resources/admin/admin/package.json src/main/resources/admin/admin/package-lock.json ./
RUN npm ci --registry=https://registry.npmmirror.com

COPY src/main/resources/admin/admin/ ./
RUN node ./node_modules/@vue/cli-service/bin/vue-cli-service.js build

FROM ${REGISTRY_MIRROR}/nginx:1.25-alpine
COPY deploy/docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /fe/dist /usr/share/nginx/html
EXPOSE 80
