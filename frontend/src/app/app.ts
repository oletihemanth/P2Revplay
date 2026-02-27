import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
//  1. Import the Global Player component
import { GlobalPlayer } from './shared/components/global-player/global-player'; 

@Component({
  selector: 'app-root',
  standalone: true,
  //  2. Add GlobalPlayer here so Angular recognizes the <app-global-player> tag!
  imports: [RouterOutlet, GlobalPlayer], 
  templateUrl: './app.html',
  styleUrl: './app.css' // (or app.component.css)
})
export class App { 
  title = 'frontend';
}