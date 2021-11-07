# BookWhale
중고책 거래 플랫폼 API 서버

## Git Flow

**Branch**   
모든 브랜치는 깃허브의 Pull Request 기능을 사용해 리뷰를 진행한 후 merge를 진행합니다.

master : prod 배포를 진행할 때 사용합니다.   
release : dev 배포할 때 사용합니다.   
develop : 코드 리뷰를 거쳐 개발이 끝난 부분을 merge할 때 진행합니다.   
feature : 기능 개발을 진행할 때 사용합니다.     
bugfix : 배포를 진행한 후 발생한 버그를 수정해야 할 때 사용합니다.     

**Commit**   
기능 개발 -> feat : [내용]    
코드 리뷰 후 리팩토링 -> refactor : [내용]   
배포 -> release : [내용]   
버그 수정 -> bugfix : [내용]     
문서 수정 -> docs : [내용]     
빌드 업무, 패키지 매니저 수정 -> chore : [내용]     
코드 포맷팅, 세미콜론 누랑, 코드 변경이 없는 경우 -> style : [내용]     

**참고 문헌**   
우아한 형제들 기술 블로그(http://woowabros.github.io/experience/2017/10/30/baemin-mobile-git-branch-strategy.html)   
더블에스 블로그(https://doublesprogramming.tistory.com/256)

