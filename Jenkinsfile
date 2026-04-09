pipeline {
    agent { label "slave_node_java" }

    environment {
        DOCKERHUB_USERNAME = 'suprit43'
        DOCKERHUB_REPO = 'hp_webapp'
        VERSION = "${BUILD_ID}"
    }

    stages {

        stage('Build Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage("Build Docker Image") {
            steps {
                sh "docker build -t ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${VERSION} ."
            }
        }

        stage("Docker Login") {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-cred',
                usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh "echo $PASS | docker login -u $USER --password-stdin"
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

        stage('Deploy via Ansible') {
            steps {
                sh '''
                cd ansible
                ansible-playbook -i inventory.ini deploy.yml --extra-vars "tag=${BUILD_ID}"
                '''
            }
        }

        stage("Cleanup") {
            steps {
                sh "docker image prune -f"
            }
        }
    }
}