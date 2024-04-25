node { 
    withCredentials([[
        $class: 'UsernamePasswordMultiBinding', 
        credentialsId: 'coachcoach-docker-username', 
        usernameVariable: 'DOCKER_USER_ID', 
        passwordVariable: 'DOCKER_USER_PASSWORD'
    ]]) { 
        stage('Pull') {
            git branch: 'main', credentialsId: 'github-username-token-credential', url: 'https://github.com/COACH-COACH/Spring-Back-End.git'
        }

        stage('Build') {
            sh(script: 'yes | sudo docker image prune -a')
            sh(script: 'sudo docker build -t coachcoach-spring .')
        }

        stage('Tag') {
            sh(script: 'sudo docker tag coachcoach-spring ${DOCKER_USER_ID}/coachcoach-spring:${BUILD_NUMBER}')
        }

        stage('Push') {
            sh(script: 'sudo docker login -u ${DOCKER_USER_ID} -p ${DOCKER_USER_PASSWORD}')
            sh(script: 'sudo docker push ${DOCKER_USER_ID}/coachcoach-spring:${BUILD_NUMBER}')
        }
      
        stage('Deploy') {
            sshagent(credentials: ['ec2-springboot-server-ssh']) {
                sh(script: 'ssh -o StrictHostKeyChecking=no ubuntu@3.39.168.72 "sudo docker rm -f coachcoach-spring-docker"')
                sh(script: 'ssh ubuntu@3.39.168.72 "sudo docker run --name coachcoach-spring-docker --env-file .env -e TZ=Asia/Seoul -p 8082:8082 -d -t ${DOCKER_USER_ID}/coachcoach-spring:${BUILD_NUMBER}"')
            }
        }

        stage('Cleaning up') { 
            sh "sudo docker rmi ${DOCKER_USER_ID}/coachcoach-spring:${BUILD_NUMBER}"
        } 
    }
}
