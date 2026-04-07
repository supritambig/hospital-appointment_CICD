pipeline {
    agent {
        label "slave_node_java"
    }

    environment {
        DOCKERHUB_USERNAME = 'suprit43'
        DOCKER_IMAGE = 'webapp'
        DOCKERHUB_REPO = 'hp_webapp'
        VERSION = "${BUILD_ID}"
        CONTAINER_NAME = 'app'
        CONTAINER_PORT = '8003'
        REQUEST_PORT = '80'
    }

    stages {
        stage("install Docker") {
            steps {
                sh "sudoapt install docker.io -y"
            }
        }
        stage("Check Docker") {
            steps {
                sh "docker --version"
            }
        }

        stage("Build Docker Image") {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage("Tag Image") {
            steps {
                sh """
                docker tag ${DOCKER_IMAGE} ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${VERSION}
                docker tag ${DOCKER_IMAGE} ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:latest
                """
            }
        }

        stage("Docker Login") {
            steps {
                withCredentials([string(credentialsId: 'dockerhub-password', variable: 'PASSWORD')]) {
                    sh "echo $PASSWORD | docker login -u ${DOCKERHUB_USERNAME} --password-stdin"
                }
            }
        }

        stage("Push Image") {
            steps {
                sh """
                docker push ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${VERSION}
                docker push ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:latest
                """
            }
        }

        stage("Remove Old Container") {
            steps {
                sh """
                docker rm -f ${CONTAINER_NAME} || true
                """
            }
        }

        stage("Run Container") {
            steps {
                sh """
                docker run -d \
                --name ${CONTAINER_NAME} \
                -p ${CONTAINER_PORT}:${REQUEST_PORT} \
                ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:latest
                """
            }
        }

        stage("Clean Up Images") {
            steps {
                sh """
                docker rmi ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${VERSION} || true
                """
            }
        }
        stage('Run Ansible Playbook') {
            agent any
            steps {
                sh 'ansible-playbook -i inventory playbook.yml'
            }
        }
    }
}