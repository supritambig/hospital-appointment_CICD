pipeline {
    agent {
        label "slave_node_java"
    }

    environment {
        DOCKER_IMAGE = "webapp"
        CONTAINER_NAME = "app"
        CONTAINER_PORT = "80"
        REQUEST_PORT = "8080"
    }

    stages {

        stage("Check Docker Version") {
            steps {
                sh "docker --version"
            }
        }

        stage("Build Docker Image") {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage("Remove Old Container") {
            steps {
                sh "docker rm -f ${CONTAINER_NAME} || true"
            }
        }

        stage("Run Docker Container") {
            steps {
                sh """
                docker run -d \
                --name ${CONTAINER_NAME} \
                -p ${REQUEST_PORT}:${CONTAINER_PORT} \
                ${DOCKER_IMAGE}
                """
            }
        }

        stage("Deploy using Ansible") {
            steps {
                sh '''
                /usr/bin/ansible-playbook -vvv -i ansible/inventory ansible/playbook.yml
                '''
            }
        }

        stage("Cleanup") {
            steps {
                sh "docker system prune -af || true"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}