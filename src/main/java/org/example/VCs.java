package org.example;

import org.example.vcplatforms.GitHub;
import org.example.vcplatforms.VCPlatform;

public enum VCs {
    GITHUB {
        @Override
        public VCPlatform getVCInstance() {
            return new GitHub();
        }
    }, GITLAB {
        @Override
        public VCPlatform getVCInstance() {
            return null;
        }
    };

    public abstract VCPlatform getVCInstance();
}
