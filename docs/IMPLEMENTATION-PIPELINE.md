# QUESTION 4 – IMPLEMENTATION DU PIPELINE

## 4.1 Integration Continue (CI)

### Etapes implementees dans le pipeline GitHub Actions:

| Etape | Status | Description |
|-------|--------|-------------|
| Checkout du code | ✅ | `actions/checkout@v4` |
| Mise en cache Maven | ✅ | `actions/cache@v3` avec `hashFiles('**/pom.xml')` |
| Compilation | ✅ | `mvn clean package -DskipTests` |
| Execution des tests | ✅ | `mvn test` avec MySQL 8.0 |
| Generation rapport coverage | ✅ | JaCoCo (integre dans `mvn test`) |
| Upload des rapports | ✅ | `actions/upload-artifact@v4` |

### Jobs CI:

```yaml
build:
  - Checkout code
  - Setup Java 17
  - Cache Maven
  - Build JAR
  - Upload JAR artifact

code-quality:
  - Checkstyle
  - PMD
  - SpotBugs

test:
  - Maven test
  - MySQL service
  - JaCoCo coverage
  - Upload test reports
```

### Criteria d'evaluation:

- ✅ **Automatisation complete**: Pipeline declenche sur push/PR
- ✅ **Pipeline vert**: Sur code valide (si tous les tests passent)
- ✅ **Pipeline rouge**: Sur code invalide (si tests echouent)

---

## 4.2 Integration SonarQube

### Configuration:

**Fichier: `sonar-project.properties`**
```properties
sonar.projectKey=taskmanager
sonar.projectName=Task Manager API
sonar.java.source=17
sonar.java.target=17
sonar.jacoco.reportPaths=target/jacoco.exec
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
sonar.qualitygate.wait=true
```

### Analyse automatique dans le pipeline:

```yaml
sonar-analysis:
  - Checkout
  - Setup Java 17
  - mvn sonar:sonar
  - Variables: SONAR_HOST_URL, SONAR_TOKEN
```

### Criteria:

| Critere | Status | Details |
|---------|--------|---------|
| Rapport visible | ✅ | Sur SonarCloud (sonarcloud.io) |
| Quality Gate | ✅ | Configuree (couverture > 70%) |
| Dette technique | ✅ | Affichee dans le dashboard |

### Configuration JaCoCo (pom.xml):
```xml
<rule>
  <element>BUNDLE</element>
  <limits>
    <limit>
      <counter>LINE</counter>
      <value>COVEREDRATIO</value>
      <minimum>0.70</minimum>
    </limit>
  </limits>
</rule>
```

---

## 4.3 Scan de securite (DevSecOps)

### Scans implementes:

| Scan | Status | Outil | Job |
|------|--------|-------|-----|
| Dependances | ✅ | OWASP Dependency Check | security-scan |
| Secrets | ✅ | GitLeaks | security-scan |
| Image Docker | ✅ | Trivy | security-scan |
| SAST | ✅ | SpotBugs | code-quality |

### Details:

#### A. OWASP Dependency Check
```yaml
- name: OWASP Dependency Check
  run: mvn org.owasp:dependency-check-maven:check
  continue-on-error: true
```
- Fichier suppression: `dependency-check-suppressions.xml`
- Rapport: `target/dependency-check-report.html`

#### B. GitLeaks (Secrets)
```yaml
- name: Run GitLeaks
  uses: gitleaks/gitleaks-action@v2
  continue-on-error: true
```
- Scan les secrets dans le code

#### C. Trivy (Docker Image)
```yaml
- name: Build Docker image for scanning
  run: docker build -t ${{ env.IMAGE_NAME }}:scan .

- name: Trivy Image Scan
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: '${{ env.IMAGE_NAME }}:scan'
    format: 'sarif'
```
- Scan les vulnerabilites dans l'image Docker

#### D. SpotBugs (SAST)
```yaml
- name: Run SpotBugs
  run: mvn spotbugs:spotbugs
```
- Analyse statique du bytecode
- Detection de bugs potentiels

---

## Resume de l'implementation

### Pipeline complet (8 jobs):

```
┌─────────────────┐
│     BUILD       │ ← Checkout, Java 17, Maven, JAR
└────────┬────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌───────┐ ┌────────────┐
│QUALITY│ │    TEST    │
│  (3)  │ │    (3)     │
└───┬───┘ └─────┬──────┘
    │           │
    └─────┬─────┘
          ▼
┌─────────────────┐
│   SONARQUBE    │ ← Analyse + Quality Gate
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   SECURITY     │ ← OWASP, Trivy, GitLeaks
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  DOCKER BUILD   │ ← GHCR Push
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    DEPLOY       │ ← GHCR
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  NOTIFICATIONS  │ ← Discord
└─────────────────┘
```

### Fichiers de configuration:

| Fichier | Role |
|---------|------|
| `.github/workflows/ci-cd.yml` | Pipeline CI/CD |
| `sonar-project.properties` | Configuration SonarQube |
| `pom.xml` | JaCoCo, SpotBugs, OWASP |
| `checkstyle.xml` | Regles code style |
| `pmd-ruleset.xml` | Regles PMD |
| `dependency-check-suppressions.xml` | Suppressions vulnerabilites |

### Secrets requis sur GitHub:

| Secret | Valeur |
|--------|--------|
| `SONAR_HOST_URL` | https://sonarcloud.io |
| `SONAR_TOKEN` | [Token SonarCloud] |
| `DISCORD_WEBHOOK` | [Webhook Discord - optionnel] |

---

*Document genere pour Task Manager Application*
*Version: 1.0.0*
