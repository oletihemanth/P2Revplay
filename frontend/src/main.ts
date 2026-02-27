
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app'; // (or whatever your app component is named)

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));