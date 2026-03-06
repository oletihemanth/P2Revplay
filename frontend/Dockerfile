# Stage 1: Build the Angular application using Node.js
FROM node:18 AS build
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy all frontend code and build it
COPY . .
RUN npm run build

# Stage 2: Serve the application using Nginx
FROM nginx:alpine
# Copy the built files from the previous stage to Nginx
# Note: If your app name is different, you might need to change /app/dist/P2Revplay to match your actual build folder!
COPY --from=build /app/dist/ /usr/share/nginx/html/

# Expose port 80 for web traffic
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]