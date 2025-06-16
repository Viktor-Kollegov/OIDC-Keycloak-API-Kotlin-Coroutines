import 'dotenv/config';

export default {
    expo: {
        name: 'frontend',
        slug: 'frontend',
        version: '1.0.0',
        sdkVersion: '53.0.0',
        platforms: ['ios', 'android', 'web'],
        scheme: 'whatif',
        extra: {
            DISCOVERY_URL: process.env.DISCOVERY_URL,
            AUTH_URL: process.env.AUTH_URL,
            TOKEN_URL: process.env.TOKEN_URL,
            CLIENT_ID: process.env.CLIENT_ID,
            REDIRECT_URI: process.env.REDIRECT_URI,
        },
    },
};
