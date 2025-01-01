import numpy as np
from sklearn.tree import DecisionTreeRegressor

if __name__ == "__main__":
    # Let's create some synthetic data for a regression task
    np.random.seed(42)
    X = np.linspace(-2, 2, 200).reshape(-1, 1)  # shape (200, 1)
    y = X[:, 0]**3 + np.random.normal(0, 1, size=X.shape[0])  # cubic with noise

    # Split into train/test
    split_idx = 150
    X_train, X_test = X[:split_idx], X[split_idx:]
    y_train, y_test = y[:split_idx], y[split_idx:]

    # Train our GBDT
    gbdt = GradientBoostingRegressor(
        n_estimators=50,
        max_depth=3,
        learning_rate=0.1,
        min_samples_split=5
    )
    gbdt.fit(X_train, y_train)

    # Evaluate on test set
    y_pred = gbdt.predict(X_test)
    mse = np.mean((y_test - y_pred) ** 2)
    print(f"Test MSE: {mse:.4f}")

    # OPTIONAL: Plot predictions vs. ground truth
    try:
        import matplotlib.pyplot as plt
        plt.scatter(X_test, y_test, color="blue", label="Test data")
        plt.scatter(X_test, y_pred, color="red", s=15, label="Predictions")
        plt.title("Simple GBDT Regression")
        plt.legend()
        plt.show()
    except ImportError:
        print("matplotlib not installed. Skipping plot.")
