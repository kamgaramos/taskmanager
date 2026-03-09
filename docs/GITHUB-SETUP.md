# Guide - Déploiement sur GitHub et Activation CI/CD

## Prérequis
- ✅ Git installé (réinstaller si nécessaire)
- ✅ Compte GitHub créé
- ✅ Projet Task Manager complet

---

## ÉTAPE 1: Créer un Repository GitHub

### 1.1 Via le Navigateur
1. Aller sur https://github.com
2. Cliquer sur **"+"** → **"New repository"**
3. Remplir le formulaire :
   - **Repository name**: `taskmanager` (ou le nom souhaité)
   - **Description**: `Spring Boot Task Manager REST API with JWT Authentication`
   - **Public** ou **Private**
   - ✅ **Add a README file** (NON - on a déjà le code)
   - ✅ **Add .gitignore** → Choisir "Java"
   - ✅ **Choose a license** → MIT ou Apache 2.0
4. Cliquer **"Create repository"**

### 1.2 Résultat
Copier l'URL du repo, exemple :
```
https://github.com/VOTRE_USERNAME/taskmanager.git
```

---

## ÉTAPE 2: Initialiser Git Localement

### 2.1 Ouvrir Git Bash
```bash
cd "c:/Users/kAMGA RAMOS/Desktop/evaluation"
```

### 2.2 Initialiser le repository
```bash
git init
```

### 2.3 Configurer Git (si première fois)
```bash
git config --global user.name "kamgaramos"
git config --global user.email "kamgaludovic13@gmail.com"
```

---

## ÉTAPE 3: Ajouter les Fichiers

### 3.1 Vérifier le statut
```bash
git status
```

### 3.2 Ajouter tous les fichiers
```bash
git add .
```

### 3.3 Créer le commit initial
```bash
git commit -m "Initial commit: Task Manager Spring Boot API with CI/CD"
```

---

## ÉTAPE 4: Connecter et Pousser sur GitHub

### 4.1 Ajouter le remote
```bash
git remote add origin https://github.com/VOTRE_USERNAME/taskmanager.git
```

### 4.2 Renommer la branche en main
```bash
git branch -M main
```

### 4.3 Pousser le code
```bash
git push -u origin main
```

**Résultat :** Votre projet est maintenant sur GitHub avec le workflow CI/CD !

---

## ÉTAPE 5: Configurer les Secrets GitHub

### 5.1 Aller dans le repository GitHub
1. Repository → **Settings** → **Secrets and variables** → **Actions**

### 5.2 Ajouter les secrets requis

| Secret | Valeur | Description |
|--------|--------|-------------|
| `SONAR_HOST_URL` | `https://sonarcloud.io` | SonarCloud (gratuit) |
| `SONAR_TOKEN` | Token SonarCloud | voir ci-dessous |

### 5.3 Créer un compte SonarCloud (Gratuit)

1. Aller sur https://sonarcloud.io
2. Se connecter avec GitHub
3. Cliquer **"Create new organization"**
4. Sélectionner votre compte GitHub
5. Créer un projet : `taskmanager`
6. Aller dans **My Account** → **Security**
7. Générer un **Token** (nom: "github-actions")
8. Copier le token et l'ajouter dans GitHub Secrets

### 5.4 Ajouter le secret SONAR_TOKEN
- **Name**: `SONAR_TOKEN`
- **Value**: Coller le token généré

---

## ÉTAPE 6: Configurer la Protection des Branches

### 6.1 Aller dans Settings
Repository → **Settings** → **Branches** → **Add branch protection rule**

### 6.2 Configurer pour "main"
- ✅ **Require pull request reviews before merging** → 2 reviews
- ✅ **Require status checks to pass before merging**
- ✅ **Require branches to be up to date**
- ✅ **Do not allow force pushes**
- ✅ **Require linear history** (optionnel)

### 6.3 Configurer pour "develop"
- ✅ **Require pull request reviews before merging** → 1 review
- ✅ **Require status checks to pass before merging**

---

## ÉTAPE 7: Vérifier le Workflow

### 7.1 Aller dans Actions
Repository → **Actions**

### 7.2 Voir le workflow
Le fichier `.github/workflows/ci-cd.yml` devrait apparaître automatiquement.

### 7.3 Déclencher le premier build
```bash
git commit --allow-empty -m "Trigger CI/CD"
git push origin main
```

### 7.4 Surveiller l'exécution
1. Aller dans **Actions** → Cliquer sur le run en cours
2. Voir les étapes : Build → Test → SonarQube → Security → Docker

---

## ÉTAPE 8: Configuration Optionnelle

### 8.1 Ajouter des Notifications Discord (Optionnel)

1. Créer un webhook Discord :
   - Server Discord → Settings → Integrations → Webhooks
   - Créer un nouveau webhook
   - Copier l'URL

2. Ajouter le secret GitHub :
   - **Name**: `DISCORD_WEBHOOK`
   - **Value**: `https://discord.com/api/webhooks/...`

### 8.2 Activer Dependabot (Optionnel)

Créer `.github/dependabot.yml` :
```yaml
version: 2
updates:
  - package-ecosystem: "maven"
    directory: "/"
    schedule:
      interval: "weekly"
```

---

## Résumé des Commandes Git

```bash
# 1. Cloner un repo existant
git clone https://github.com/user/repo.git

# 2. Voir le statut
git status

# 3. Voir les différences
git diff

# 4. Ajouter des fichiers modifiés
git add nom_fichier.txt
git add .  # tous les fichiers

# 5. Créer un commit
git commit -m "Description du commit"

# 6. Pousser vers GitHub
git push origin main

# 7. Récupérer les dernières modifications
git pull origin main

# 8. Créer une branche
git checkout -b feature/ma-fonctionnalite

# 9. Basculer de branche
git checkout develop

# 10. Fusionner une branche
git merge feature/ma-fonctionnalite
```

---

## Dépannage

### Erreur: "Authentication failed"
```bash
# Régénérer le token GitHub
# Settings → Developer settings → Personal access tokens → Tokens (classic)
# Créer un nouveau token avec repo permissions
git remote set-url origin https://VOTRE_TOKEN@github.com/user/repo.git
```

### Erreur: "refusing to merge unrelated histories"
```bash
git pull origin main --allow-unrelated-histories
git push origin main
```

---

## Prochaines Étapes Après le Déploiement

1. ✅ Le pipeline va automatiquement :
   - Compiler le code
   - Exécuter les tests
   - Analyser avec SonarQube
   - Scanner les vulnérabilités
   - Construire l'image Docker

2. ⏳ Pour le déploiement automatique :
   - Ajouter les credentials cloud (AWS, GCP, Azure)
   - Ou utiliser des scripts Kubernetes/Ansible
   - Ou utiliser GitHub Deployments

---

*Document généré pour Task Manager Application*
*Version: 1.0.0*

