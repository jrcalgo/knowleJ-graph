package ai.knowlej.Learning;

import deeplearning4j.*;

public class KBChainOptimizer {
    public KBChainOptimizer() {

    }

    private class Architecture {
        private int[] layers;
        private int[] activations;
        private int[] dropouts;

        public Architecture(int[] layers, int[] activations, int[] dropouts) {
            this.layers = layers;
            this.activations = activations;
            this.dropouts = dropouts;
        }

        public int[] getLayers() {
            return this.layers;
        }

        public int[] getActivations() {
            return this.activations;
        }

        public int[] getDropouts() {
            return this.dropouts;
        }

        public void setLayers(int[] layers) {
            this.layers = layers;
        }

        public void setActivations(int[] activations) {
            this.activations = activations;
        }

        public void setDropouts(int[] dropouts) {
            this.dropouts = dropouts;
        }
    }
}
