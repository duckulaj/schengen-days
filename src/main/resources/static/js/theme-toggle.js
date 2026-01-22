// Theme management
(function() {
  'use strict';

  const THEME_KEY = 'schengen-theme';
  const DARK_THEME = 'dark';
  const LIGHT_THEME = 'light';

  // Get saved theme or use system preference
  function getInitialTheme() {
    const saved = localStorage.getItem(THEME_KEY);
    if (saved === DARK_THEME || saved === LIGHT_THEME) {
      return saved;
    }
    
    // Check system preference
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return DARK_THEME;
    }
    
    return LIGHT_THEME;
  }

  // Detect device type based on user agent and screen width
  function detectDeviceType() {
    const ua = navigator.userAgent;
    const width = window.innerWidth;

    if (/(tablet|ipad|playbook|silk)|(android(?!.*mobi))/i.test(ua)) {
      return 'tablet';
    }
    
    if (/Mobile|iP(hone|od)|Android|BlackBerry|IEMobile|Kindle|Silk-Accelerated|(hpwOS)/i.test(ua)) {
      return 'mobile';
    }

    // Secondary check based on width if UA detection is ambiguous
    if (width <= 600) return 'mobile';
    if (width <= 1024) return 'tablet';
    
    return 'pc';
  }

  // Apply device theme
  function applyDeviceTheme() {
    const deviceType = detectDeviceType();
    document.documentElement.setAttribute('data-device', deviceType);
    console.log('Detected device type:', deviceType);
  }

  // Apply theme to document
  function applyTheme(theme) {
    if (theme === DARK_THEME) {
      document.documentElement.setAttribute('data-theme', 'dark');
    } else {
      document.documentElement.removeAttribute('data-theme');
    }
    localStorage.setItem(THEME_KEY, theme);
  }

  // Toggle between themes
  function toggleTheme() {
    const current = localStorage.getItem(THEME_KEY) || LIGHT_THEME;
    const newTheme = current === LIGHT_THEME ? DARK_THEME : LIGHT_THEME;
    applyTheme(newTheme);
    updateToggleButton();
  }

  // Update toggle button text and icon
  function updateToggleButton() {
    const toggle = document.getElementById('theme-toggle');
    if (!toggle) return;

    const current = localStorage.getItem(THEME_KEY) || LIGHT_THEME;
    const icon = toggle.querySelector('.theme-toggle-icon');
    const text = toggle.querySelector('.theme-toggle-text');

    if (current === DARK_THEME) {
      if (icon) icon.textContent = '☀️';
      if (text) text.textContent = 'Light';
    } else {
      if (icon) icon.textContent = '🌙';
      if (text) text.textContent = 'Dark';
    }
  }

  // Initialize theme immediately (prevent FOUC)
  const initialTheme = getInitialTheme();
  applyTheme(initialTheme);
  applyDeviceTheme();

  // Setup toggle button when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
      updateToggleButton();
      const toggle = document.getElementById('theme-toggle');
      if (toggle) {
        toggle.addEventListener('click', toggleTheme);
      }
    });
  } else {
    updateToggleButton();
    const toggle = document.getElementById('theme-toggle');
    if (toggle) {
      toggle.addEventListener('click', toggleTheme);
    }
  }

  // Export functions for external use
  window.ThemeManager = {
    toggle: toggleTheme,
    apply: applyTheme,
    getCurrent: function() {
      return localStorage.getItem(THEME_KEY) || LIGHT_THEME;
    }
  };
})();
