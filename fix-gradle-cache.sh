#!/bin/bash

set -e

PROJECT_ROOT="/Users/praveenchauhan/Documents/Personal_Financial_Tracking/expense-tracker"
GRADLE_PROJECT_DIR="$PROJECT_ROOT/android"

echo "========================================="
echo "Gradle Transform Cache Fix Script"
echo "========================================="

# Step 1: Stop all Gradle daemons
echo ""
echo "[1/4] Stopping all Gradle daemons..."
cd "$GRADLE_PROJECT_DIR"
./gradlew --stop 2>/dev/null || true
pkill -f "GradleDaemon" 2>/dev/null || true
echo "✓ Gradle daemons stopped"

# Step 2: Delete the specific corrupt transform directory
CORRUPT_DIR="/Users/praveenchauhan/.gradle/caches/8.9/transforms/d223969eb53ab8e82104fd70e9df0472"
echo ""
echo "[2/4] Deleting corrupt transform directory: $CORRUPT_DIR"
if [ -d "$CORRUPT_DIR" ]; then
    rm -rf "$CORRUPT_DIR"
    echo "✓ Corrupt directory deleted"
else
    echo "⚠ Directory not found (may have been already cleaned)"
fi

# Step 3: Fallback - delete entire transforms cache if needed
TRANSFORMS_DIR="/Users/praveenchauhan/.gradle/caches/8.9/transforms"
echo ""
echo "[3/4] Checking transforms cache..."
if [ -d "$TRANSFORMS_DIR" ] && [ "$(ls -A $TRANSFORMS_DIR 2>/dev/null)" ]; then
    echo "⚠ Transforms cache still contains files, deleting entire cache..."
    rm -rf "$TRANSFORMS_DIR"
    echo "✓ Transforms cache deleted"
else
    echo "✓ Transforms cache already clean"
fi

# Step 4: Run clean build to verify fix
echo ""
echo "[4/4] Running ./gradlew clean build..."
echo "-----------------------------------------"

cd "$GRADLE_PROJECT_DIR"
if ./gradlew clean build; then
    echo ""
    echo "========================================="
    echo "✅ SUCCESS: Build completed successfully!"
    echo "========================================="
    exit 0
else
    echo ""
    echo "========================================="
    echo "❌ FAILURE: Build failed"
    echo "========================================="
    exit 1
fi
