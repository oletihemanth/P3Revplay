pipeline {
    agent any
    
    environment {
        // Services to build
        BACKEND_SERVICES = 'revplay-eureka-server revplay-config-server revplay-api-gateway revplay-auth-service revplay-catalog-service revplay-user-service revplay-analytics-service revplay-favorite-service revplay-playback-service revplay-playlist-service'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build Backend Apps (Maven)') {
            steps {
                dir('Revplay-backend') {
                    script { 
                        def services = env.BACKEND_SERVICES.split(' ')
                        for (service in services) {
                            dir(service) {
                                echo "Building ${service} with Maven..."
                                if (isUnix()) {
                                    sh 'mvn clean package -DskipTests'
                                } else {
                                    bat 'mvn clean package -DskipTests'
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Build Frontend App (Angular)') {
            steps {
                dir('Revplay-frontend') {
                    echo "Building Angular Application..."
                    script { // <-- Added script block here
                        if (isUnix()) {
                            sh 'npm install'
                            sh 'npm run build --configuration=production'
                        } else {
                            bat 'npm install'
                            bat 'npm run build --configuration=production'
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                // Build Backend Docker Images
                dir('Revplay-backend') {
                    script { // <-- Added script block here
                        if (isUnix()) {
                            sh 'docker-compose build'
                        } else {
                            bat 'docker-compose build'
                        }
                    }
                }
                
                // Build Frontend Docker Image
                dir('Revplay-frontend') {
                    script { // <-- Added script block here
                        if (isUnix()) {
                            sh 'docker build -t revplay-frontend:latest .'
                        } else {
                            bat 'docker build -t revplay-frontend:latest .'
                        }
                    }
                }
            }
        }

        stage('Deploy to Docker') {
            steps {
                dir('Revplay-backend') {
                    echo "Starting Backend Services..."
                    script { // <-- Added script block here
                        if (isUnix()) {
                            sh 'docker-compose up -d'
                        } else {
                            bat 'docker-compose up -d'
                        }
                    }
                }
                
                dir('Revplay-frontend') {
                    echo "Starting Frontend Container..."
                    script {
                        if (isUnix()) {
                            sh 'docker stop revplay-frontend || true'
                            sh 'docker rm revplay-frontend || true'
                            sh 'docker run -d -p 80:80 --name revplay-frontend revplay-frontend:latest'
                        } else {
                            bat '''
                                docker stop revplay-frontend || exit 0
                                docker rm revplay-frontend || exit 0
                                docker run -d -p 80:80 --name revplay-frontend revplay-frontend:latest
                            '''
                        }
                    }
                }
            }
        }
    }
}