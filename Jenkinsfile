node {
    withCredentials([[$class: 'UsernamePasswordMultiBinding',
        credentialsId: 'coachcoach-docker-username',
        usernameVariable: 'DOCKER_USER_ID',
        passwordVariable: 'DOCKER_USER_PASSWORD'
    ]]) {
        stage('Pull') {
            git branch: 'main', credentialsId: 'github-username-token-credential', url: 'https://github.com/COACH-COACH/Spring-Back-End.git'
        }

        stage('Build') {
       		sh 'yes | sudo docker image prune -a'
            sh 'sudo docker-compose --env-file=/var/lib/jenkins/workspace/.env build'
        }
        
       	stage('Push') {
            sh 'sudo docker login -u $DOCKER_USER_ID -p $DOCKER_USER_PASSWORD'
            sh 'sudo docker-compose push'      	
       	}

        stage('Deploy') {
            sshagent(credentials: ['ec2-springboot-server-ssh']) {
                sh 'ssh -o StrictHostKeyChecking=no ubuntu@3.39.168.72 "sudo docker-compose -f /path/to/docker-compose.yml pull"'
                sh 'ssh -o StrictHostKeyChecking=no ubuntu@3.39.168.72 "sudo docker-compose -f /path/to/docker-compose.yml up -d"'
            }
        }
        
        stage('Cleaning Up) {
        	sh 'sudo docker rmi ${DOCKER_USER_ID}/spring-back-end-app:1.0'
        	sh 'sudo docker rmi ${DOCKER_USER_ID}/spring-back-end-filebeat:1.0'
        }
    }
}
