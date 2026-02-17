# Git Setup Guide for BlueSphere

## 🎯 Quick Setup (3 Steps)

### Step 1: Create GitHub Repository
1. Go to: https://github.com/new
2. Repository name: `BlueSphere`
3. Description: `Weather-based logistics risk platform - Predicts delivery delays using real-time weather data`
4. Choose: **Public** or **Private**
5. **DO NOT** initialize with README, .gitignore, or license (we already have these)
6. Click **"Create repository"**

### Step 2: Initialize Local Git Repository
Run these commands in PowerShell:

```powershell
# Navigate to project directory
cd c:\Users\dell\.gemini\antigravity\scratch\sphere

# Initialize git repository
git init

# Add all files
git add .

# Create first commit
git commit -m "Initial commit: BlueSphere logistics risk platform

- Spring Boot 4.0.2 application
- Weather-based delivery risk analysis
- REST API for Power BI integration
- Automated data ingestion from OpenWeatherMap
- Risk calculation engine (precipitation, wind, visibility)
- H2 database for development
- Comprehensive documentation"
```

### Step 3: Link to GitHub and Push
Replace `YOUR_USERNAME` with your actual GitHub username:

```powershell
# Add remote repository
git remote add origin https://github.com/YOUR_USERNAME/BlueSphere.git

# Rename branch to main (if needed)
git branch -M main

# Push to GitHub
git push -u origin main
```

---

## 📋 Complete Command Sequence

Copy and paste this entire block (update YOUR_USERNAME):

```powershell
cd c:\Users\dell\.gemini\antigravity\scratch\sphere
git init
git add .
git commit -m "Initial commit: BlueSphere logistics risk platform"
git remote add origin https://github.com/YOUR_USERNAME/BlueSphere.git
git branch -M main
git push -u origin main
```

---

## 🔐 Authentication Options

### Option 1: Personal Access Token (Recommended)
1. Go to: https://github.com/settings/tokens
2. Click **"Generate new token (classic)"**
3. Name: `BlueSphere Development`
4. Select scopes: `repo` (all checkboxes under repo)
5. Click **"Generate token"**
6. **Copy the token** (you won't see it again!)
7. When pushing, use token as password:
   - Username: your GitHub username
   - Password: paste the token

### Option 2: GitHub CLI
```powershell
# Install GitHub CLI
winget install --id GitHub.cli

# Authenticate
gh auth login

# Push repository
gh repo create BlueSphere --public --source=. --remote=origin --push
```

### Option 3: SSH Key
```powershell
# Generate SSH key
ssh-keygen -t ed25519 -C "your_email@example.com"

# Copy public key
cat ~/.ssh/id_ed25519.pub | clip

# Add to GitHub: https://github.com/settings/keys
# Then use SSH URL instead:
git remote add origin git@github.com:YOUR_USERNAME/BlueSphere.git
```

---

## 🎨 Recommended Repository Settings

### After Creating Repository:

1. **Add Topics** (for discoverability):
   - `spring-boot`
   - `weather-api`
   - `logistics`
   - `risk-analysis`
   - `java`
   - `power-bi`
   - `openweathermap`

2. **Add Description**:
   ```
   🌍 Weather-based logistics risk platform that predicts delivery delays 
   using real-time weather data. Built with Spring Boot, integrates with 
   Power BI for dashboard visualization.
   ```

3. **Set Repository Details**:
   - Website: (your deployment URL if you have one)
   - Enable Issues
   - Enable Discussions (optional)

---

## 📝 .gitignore Already Included

The project already has a `.gitignore` file that excludes:
- Build artifacts (`build/`, `target/`)
- IDE files (`.idea/`, `.vscode/`, `*.iml`)
- Gradle cache (`.gradle/`)
- OS files (`.DS_Store`, `Thumbs.db`)
- Logs (`*.log`)

---

## 🚀 Future Git Workflow

### Making Changes
```powershell
# Check status
git status

# Add specific files
git add src/main/java/com/bluesphere/map/service/NewService.java

# Or add all changes
git add .

# Commit with message
git commit -m "Add new feature: XYZ"

# Push to GitHub
git push
```

### Creating Branches
```powershell
# Create feature branch
git checkout -b feature/new-risk-algorithm

# Make changes, commit
git add .
git commit -m "Implement new risk scoring algorithm"

# Push branch
git push -u origin feature/new-risk-algorithm

# Create Pull Request on GitHub
```

---

## 📊 Recommended README Badges

Add these to the top of your README.md on GitHub:

```markdown
# BlueSphere

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)
![Build](https://img.shields.io/badge/Build-Passing-success)

Weather-based logistics risk platform for delivery optimization
```

---

## 🐛 Troubleshooting

### "Authentication failed"
- Use Personal Access Token instead of password
- Or use GitHub CLI: `gh auth login`

### "Remote origin already exists"
```powershell
git remote remove origin
git remote add origin https://github.com/YOUR_USERNAME/BlueSphere.git
```

### "Nothing to commit"
```powershell
# Check what's ignored
git status --ignored

# Force add if needed
git add -f filename
```

### Large files warning
```powershell
# Remove large files from staging
git reset HEAD path/to/large/file

# Add to .gitignore
echo "path/to/large/file" >> .gitignore
```

---

## 📦 What Will Be Pushed

Your repository will include:
- ✅ 12 Java source files
- ✅ 7 Documentation files (README, QUICKSTART, etc.)
- ✅ Configuration files (build.gradle, application.properties)
- ✅ Gradle wrapper
- ✅ Postman collection
- ✅ SQL schema

**Total**: ~50KB of code + documentation

---

## 🎉 After Pushing

Your GitHub repository will show:
- Professional README with architecture diagrams
- Complete documentation
- Production-ready Spring Boot code
- Easy setup instructions for contributors

**Share your repository**: `https://github.com/YOUR_USERNAME/BlueSphere`

---

**Ready to push?** Follow Step 1-3 above! 🚀
