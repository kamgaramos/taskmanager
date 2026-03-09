# Guide - Configuration des Secrets GitHub

Ce guide explique comment configurer les secrets necessaires pour le pipeline CI/CD.

---

## Etape 1: Creer un compte SonarCloud (Gratuit)

### 1.1 Inscription
1. Ouvrir https://sonarcloud.io
2. Cliquer sur **"Log in"** ou **"Sign up"**
3. Choisir **"With GitHub"** pour se connecter avec son compte GitHub
4. Autoriser l'acces

### 1.2 Creer une Organisation
1. Une fois connecte, cliquer sur **"Create new organization"**
2. Selectionner votre compte GitHub
3. Choisir un nom pour l'organisation (ex: `kamgaramos`)
4. Cliquer **"Continue"**

### 1.3 Creer un Projet
1. Cliquer sur **"Create new project"**
2. Selectionner le repository `taskmanager`
3. Cliquer **"Set up"**

---

## Etape 2: Generer un Token SonarCloud

### 2.1 Acceder aux parametres
1. En haut a droite, cliquer sur votre profil
2. Selectionner **"My Account"**
3. Cliquer sur l'onglet **"Security"**

### 2.2 Creer le token
1. Dans la section **"Tokens"**, saisir un nom (ex: `github-actions`)
2. Cliquer sur **"Generate"**
3. **IMPORTANT**: Copier le token immediatement

Le token resemble a: `sqg_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`

---

## Etape 3: Ajouter les Secrets sur GitHub

### 3.1 Ajouter SONAR_HOST_URL
1. Aller sur: https://github.com/kamgaramos/taskmanager/settings/secrets/actions
2. Cliquer sur **"New repository secret"**
3. **Name**: `SONAR_HOST_URL`
4. **Secret**: `https://sonarcloud.io`
5. Cliquer **"Add secret"**

### 3.2 Ajouter SONAR_TOKEN
1. Cliquer sur **"New repository secret"**
2. **Name**: `SONAR_TOKEN`
3. **Secret**: [Coller le token genere]
4. Cliquer **"Add secret"**

---

## Depannage

### Erreur: "exit code 126" - Permission denied
- Le fichier `./mvnw` n'a pas les permissions
- Solution: Utiliser `mvn` au lieu de `./mvnw` dans le workflow

### Erreur: "Project not found"
- Creer le projet sur SonarCloud avec le nom "taskmanager"

### Erreur: "Token is invalid"
- Regenerer un nouveau token sur SonarCloud aa
- Mettre a jour le secret sur GitHub

---

*Document genere pour Task Manager Application*

