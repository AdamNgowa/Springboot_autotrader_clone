# --- STAGE 1: Build the Application ---
# Use the lightweight Node.js 22 Alpine image and name this stage 'build'
FROM node:22-alpine AS build

# Set the working directory inside the container to /app
WORKDIR /app

# Copy only the dependency manifests first to leverage Docker layer caching
COPY package*.json ./

# Install dependencies strictly based on package-lock.json
RUN npm ci

# Copy the rest of the application source code into the container
COPY . .

# Compile and bundle the application into static files
RUN npm run build


# --- STAGE 2: Serve the Application ---
# Use a lightweight Nginx web server image for the final container
FROM nginx:alpine

# Copy the compiled static files into Nginx's public folder
COPY --from=build /app/dist /usr/share/nginx/html

# Replace Nginx's default configuration with our SPA configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Document that this container listens for HTTP traffic on port 80
EXPOSE 80

# Start Nginx in the foreground so the Docker container remains running
CMD ["nginx", "-g", "daemon off;"]