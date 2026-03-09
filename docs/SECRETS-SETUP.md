# Guide - Configuration des Secrets GitHub

Ce guide explique comment configurer les secrets nécessaires pour le pipeline CI/CD.

---

## Étape 1: Créer un compte SonarCloud (Gratuit)

### 1.1 Inscription
1. Ouvrir https://sonarcloud.io
2. Cliquer sur **"Log in"** ou **"Sign up"**
3. Choisir **"With GitHub"** pour se connecter avec son compte GitHub
4. Autoriser l'accès

### 1.2 Créer une Organisation
1. Une fois connecté, cliquer sur **"Create new organization"**
2. Sélectionner votre compte GitHub
3. Choisir un nom pour l'organisation (ex: `kamgaramos`)
4. Cliquer **"Continue"**

### 1.3 Créer un Projet
1. Cliquer sur **"Create new project"**
2. Choisir **"Use existing repository"** ou créer un nouveau
3. Sélectionner le repository `taskmanager`
4. Cliquer **"Set up"** → **"With GitHub Actions"**

---

## Étape 2: Générer un Token SonarCloud

### 2.1 Accéder aux paramètres
1. En haut à droite, cliquer sur votre profil
2. Sélectionner **"My Account"**
3. Cliquer sur l'onglet **"Security"**

### 2.2 Créer le token
1. Dans la section **"Tokens"**, saisir un nom (ex: `github-actions`)
2. Cliquer sur **"Generate"**
3. **IMPORTANT**: Copier le token immédiatement (il ne sera affiché qu'une seule fois!)

Le token ressemble à quelque chose comme:
```
sqg_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

---

## Étape 3: Ajouter les Secrets sur GitHub

### 3.1 Accéder aux secrets
1. Aller sur https://github.com/kamgaramos/taskmanager
2. Cliquer sur l'onglet **"Settings"** (en haut)
3. Dans le menu de gauche, cliquer sur **"Secrets and variables"**
4. Cliquer sur **"Actions"**

### 3.2 Ajouter SONAR_HOST_URL
1. Cliquer sur **"New repository secret"**
2. Dans le champ **Name**, saisir: `SONAR_HOST_URL`
3. Dans le champ **Secret**, saisir: `https://sonarcloud.io`
4. Cliquer sur **"Add secret"**

### 3.3 Ajouter SONAR_TOKEN
1. Cliquer sur **"New repository secret"**
2. Dans le champ **Name**, saisir: `SONAR_TOKEN`
3. Dans le champ **Secret**, coller le token généré à l'étape 2.2
4. Cliquer sur **"Add secret"**

---

## Étape 4: Vérifier la Configuration

### 4.1 Voir les secrets
- Les secrets apparaissent maintenant dans la liste
- **Note**: Les valeurs ne sont jamais affichées (seulement *****)

### 4.2 Tester le pipeline
1. Faire un petit changement dans le code
2. Commiter et pusher:
```bash
git add .
git commit -m "Test CI/CD with secrets"
git push origin main
```
3. Aller dans l'onglet **"Actions"** sur GitHub
4. Vérifier que le job **"SonarQube Analysis"** fonctionne

---

## Configuration Optionnelle - Discord

Pour recevoir des notifications Discord:

### Créer un Webhook Discord
1. Ouvrir votre serveur Discord
2. Aller dans **Server Settings** → **Integrations** → **Webhooks**
3. Cliquer **"New Webhook"**
4. Choisir le salon (channel)
5. Copier l'URL du webhook

### Ajouter le secret Discord
1. Retourner sur GitHub → Settings → Secrets → Actions
2. Cliquer **"New repository secret"**
3. **Name**: `DISCORD_WEBHOOK`
4. **Secret**: Coller l'URL du webhook
5. Cliquer **"Add secret"**

---

## Résumé des Secrets

| Secret | Valeur | Requis | Description |
|--------|--------|--------|-------------|
| `SONAR_HOST_URL` | `https://sonarcloud.io` | ✅ Oui | URL SonarCloud |
| `SONAR_TOKEN` | Token généré | ✅ Oui | Token d'authentification |
| `DISCORD_WEBHOOK` | URL webhook | ❌ Non | Notifications Discord |

---

## Dépannage

### Erreur: "Project not found"
- Vérifier que le projet existe sur SonarCloud
- Vérifier que le SONAR_PROJECT_KEY correspond (dans ci-cd.yml: `taskmanager`)
- **Solution**: Créer le projet sur SonarCloud avec le nom "taskmanager"

### Erreur: "Token is invalid"
- Régénérer un nouveau token sur SonarCloud
- Mettre à jour le secret sur GitHub
- **Solution**:
  1. Aller sur sonarcloud.io → My Account → Security
  2. Supprimer l'ancien token et en créer un nouveau
  3. Mettre à jour le secret sur GitHub

### Le pipeline ne se déclenche pas
- Vérifier que le fichier workflow est dans `.github/workflows/`
- Vérifier que les triggers sont corrects (push sur main/develop)
- **Solution**:
  1. Vérifier que le fichier est dans `.github/workflows/ci-cd.yml`
  2. Les triggers doivent être: push sur main/develop, pull_request, release, schedule, workflow_dispatch

### Erreur: "Permission denied" pour Docker
- Le push vers GHCR nécessite des permissions
- **Solution**: Vérifier que le token GitHub a les bonnes permissions (repo, write:packages)

### Erreur: Tests échouent
- Vérifier la configuration de la base de données MySQL
- **Solution**: Le pipeline utilise MySQL 8.0 avec les credentials root/root

---

## Prochaines Étapes

Après avoir configuré les secrets:

1. ✅ Le pipeline CI/CD est complet
2. ✅ L'analyse SonarQube fonctionnera
3. ✅ Les notifications Discord (si configuré) fonctionneront
4. ⏳ Pour le déploiement automatique, ajouter des secrets cloud (AWS, GCP, Azure)

---

*Document généré pour Task Manager Application*

