# Git branch 구조
```
main        → 운영 배포용 (절대 직접 개발 X)
develop     → 개발 통합 브랜치
feature/*   → 기능 개발
hotfix/*    → 운영 긴급 수정
release/*   → 배포 준비
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
```
feat: 로그인 API 추가
fix: 403 에러 수정
refactor: SecurityConfig 구조 개선
docs: Swagger 문서 추가
```