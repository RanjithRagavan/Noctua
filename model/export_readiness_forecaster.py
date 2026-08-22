#!/usr/bin/env python3
"""
Export the Noctua readiness forecaster to an ExecuTorch `.pte` program.

Model contract (must stay in sync with WellnessFeatures.toVector()):
    input  : float32 [1, 8]  — normalized wellness features
    output : float32 [1, 1]  — predicted readiness score in [0, 100]

Usage:
    pip install torch executorch
    python export_readiness_forecaster.py

Then copy `readiness_forecaster.pte` into the Android app's assets or files
dir and point ExecuTorchForecaster at it.

Training: this script exports an *architecture* with a small synthetic
warm-start so the artifact runs end-to-end. For a personalized model, train
`ReadinessForecasterNet` on the user's own Oura history (collected on-device
or exported from Oura Cloud) and re-export — the contract stays the same.
"""

import torch
import torch.nn as nn

FEATURES = 8  # WellnessFeatures.VECTOR_SIZE


class ReadinessForecasterNet(nn.Module):
    """Tiny MLP — small enough to run in <5 ms on any modern phone."""

    def __init__(self) -> None:
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(FEATURES, 32),
            nn.ReLU(),
            nn.Linear(32, 16),
            nn.ReLU(),
            nn.Linear(16, 1),
        )

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        return self.net(x)


def export() -> None:
    model = ReadinessForecasterNet().eval()

    # --- Synthetic warm-start ---------------------------------------------
    # Heuristic target: start at 78, debt hurts, HRV z-score helps, etc.
    # (Same directions as LinearHeuristicForecaster — replace with real
    # training on user history for production.)
    torch.manual_seed(7)
    x = torch.rand(2048, FEATURES) * 2 - 1
    w = torch.tensor([-14.0, 10.0, 8.0, 12.0, 8.0, -12.0, 6.0, 3.0])
    y = (78.0 + x @ w + torch.randn(2048) * 2.0).unsqueeze(1)

    opt = torch.optim.Adam(model.parameters(), lr=3e-3)
    for epoch in range(300):
        opt.zero_grad()
        loss = nn.functional.mse_loss(model(x), y)
        loss.backward()
        opt.step()
    print(f"warm-start loss: {loss.item():.3f}")

    # --- ExecuTorch export --------------------------------------------------
    try:
        from executorch.exir import to_edge
        from torch.export import export as torch_export

        example = (torch.rand(1, FEATURES),)
        aten = torch_export(model, example)
        edge = to_edge(aten)
        program = edge.to_executorch()
        with open("readiness_forecaster.pte", "wb") as f:
            f.write(program.buffer)
        print("wrote readiness_forecaster.pte")
    except ImportError:
        # Fall back to TorchScript so the script still produces *something*
        # useful on machines without the ExecuTorch toolchain installed.
        scripted = torch.jit.trace(model, torch.rand(1, FEATURES))
        scripted.save("readiness_forecaster.pt")
        print("executorch not installed — wrote TorchScript readiness_forecaster.pt instead")


if __name__ == "__main__":
    export()
