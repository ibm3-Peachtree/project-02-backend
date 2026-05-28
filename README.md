# Git branch 구조
```
main        → 운영 배포용 (절대 직접 개발 X)
develop     → 개발 통합 브랜치
feature/*   → 기능 개발
hotfix/*    → 운영 긴급 수정
release/*   → 배포 준비
```
```
main
develop
feature/auth
feature/routine
feature/notification
feature/report
feature/user
feature/feed
```


# 기본 흐름
```
feature 생성
   ↓
develop merge
   ↓
release 브랜치
   ↓
main 배포
```

# commit 규칙

| 타입       | 의미       |
| -------- | -------- |
| feat     | 기능 추가    |
| fix      | 버그 수정    |
| refactor | 코드 구조 개선 |
| chore    | 설정 변경    |
| docs     | 문서       |
| test     | 테스트      |


예시)
```
feat: 로그인 API 추가
fix: 403 에러 수정
refactor: SecurityConfig 구조 개선
docs: Swagger 문서 추가
```

