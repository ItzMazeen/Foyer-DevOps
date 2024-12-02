pipeline {
    agent any

    environment {
        GIT_CREDENTIALS_ID = 'Git'
        DOCKERHUB_CREDENTIALS_ID = 'DockerHub'
        DOCKER_IMAGE = 'itzmazeen/devops'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'MazenAljane_5ARCTIC6_G2',
                    url: 'https://github.com/m0rphMZ/5ARCTIC6_G2_Foyer.git',
                    credentialsId: GIT_CREDENTIALS_ID
            }
        }

        stage('Nettoyage du projet') {
            steps {
                echo 'Nettoyage du projet...'
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo 'Starting SonarQube service...'
                    sh 'docker-compose up -d sonarqube'
                    // Wait for SonarQube to be fully up and running
                    sleep(30)  // Adjust sleep time as necessary

                    echo 'Running SonarQube analysis...'
                    withSonarQubeEnv('sq1') {
                        sh 'mvn sonar:sonar -Dsonar.projectKey=5ARCTIC6_G2_Foyer'
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
        }

        stage('Créer le livrable') {
            steps {
                echo 'Création du livrable...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo 'Starting Nexus service...'
                    sh 'docker-compose up -d nexus'
                    // Wait for nexus to be fully up and running
                    sleep(30)  // Adjust sleep time as necessary
                echo 'Deploying to Nexus...'
                    sh 'mvn deploy -DskipTests'
                }
            }


        stage('Build Docker Image') {
            steps {
                script {
                    def app = docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKERHUB_CREDENTIALS_ID) {
                        def app = docker.image("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                        app.push()
                    }
                }
            }
        }

        stage('Run Application with Docker Compose') {
            steps {
                script {
                    // Remove any existing containers (if needed)
                    sh 'docker-compose down || true'

                    // Export the BUILD_NUMBER environment variable for use in Docker Compose
                    sh "export BUILD_NUMBER=${env.BUILD_NUMBER}"

                    // Start the application using Docker Compose
                    sh 'docker-compose up -d --build'
                }
            }
        }
    }

    post {
        always {
            emailext(
                subject: "${currentBuild.fullDisplayName} Pipeline Status: ${currentBuild.result}",
                body: """
                    Bonjour,
                    <p>Le build ${currentBuild.fullDisplayName} a été lancé.</p>
                    <p>Status: ${currentBuild.result}</p>
                    <p>Build Number: ${currentBuild.number}</p>
                    <p>Check the <a href="${env.BUILD_URL}">Console Output</a> for details.</p>
                """,
                to: 'mazeenaljanepro@gmail.com',
                from: 'jenkins@example.com',
                replyTo: 'jenkins@example.com',
                mimeType: 'text/html'
            )
        }
    }
}
