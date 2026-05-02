ARG BASE_NODE_IMAGE=node:18-alpine
ARG BASE_NGINX_IMAGE=nginx:1.25-alpine

FROM ${BASE_NODE_IMAGE} AS build
WORKDIR /fe
COPY src/main/resources/admin/admin/package.json src/main/resources/admin/admin/package-lock.json ./
RUN npm ci
COPY src/main/resources/admin/admin/ ./
RUN node ./node_modules/@vue/cli-service/bin/vue-cli-service.js build

FROM ${BASE_NGINX_IMAGE}
COPY deploy/frontend-ci.nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /fe/dist /usr/share/nginx/html
EXPOSE 80
