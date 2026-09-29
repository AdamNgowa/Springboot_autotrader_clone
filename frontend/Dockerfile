# --- STAGE 1: Build the Application ---

FROM node:22-alpine AS build

WORKDIR /app

# Vite environment variables are evaluated during the build.
# ARG - allows docker to receive a value when the image is built
# ENV - makes that value available to the npm run build process
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

# Copy dependency manifests first to leverage Docker layer caching.
COPY package*.json ./

# Install exactly the dependencies recorded in package-lock.json.
RUN npm ci

# Copy the application source code.
COPY . .

# Build the production frontend.
RUN npm run build


# --- STAGE 2: Serve the Application ---

FROM nginx:alpine

# Copy the compiled static application into Nginx's public directory.
COPY --from=build /app/dist /usr/share/nginx/html

# Use the application's SPA configuration.
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]