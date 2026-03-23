/* ========= Workspace Manager SPK (tabs + iframes) ========= */
/*
  Padrão SPK - versão revisada
  Recursos:
  ✅ Abas dinâmicas com iframe
  ✅ Evita abas duplicadas
  ✅ Recarregar / fechar aba com segurança
  ✅ Altura automática responsiva
  ✅ URL absoluta confiável
  ✅ Integração com layout SPK
*/

(function () {
  'use strict';

  const SELECTORS = {
    tabs: '#workspaceTabs',
    content: '#workspaceContent',
    homeTabLink: '#tab-home-link',
    homePane: '#tab_home',
    frames: 'iframe.workspace-frame'
  };

  function hash(str) {
    let h = 0;
    for (let i = 0; i < str.length; i++) {
      h = (h << 5) - h + str.charCodeAt(i);
      h |= 0;
    }
    return 'tab_' + Math.abs(h);
  }

  function toAbsoluteUrl(url) {
    if (!url) return null;
    try {
      return new URL(url, window.location.origin).toString();
    } catch (e) {
      return window.location.origin + (url.startsWith('/') ? url : '/' + url);
    }
  }

  function getWorkspaceHeight() {
    const $content = $(SELECTORS.content);
    if (!$content.length) return 500;

    const offset = $content.offset();
    const top = offset ? offset.top : 0;
    const viewportHeight = window.innerHeight || document.documentElement.clientHeight || 800;

    return Math.max(380, viewportHeight - top - 24);
  }

  function adjustIframesHeight() {
    const height = getWorkspaceHeight();
    $(`${SELECTORS.content} ${SELECTORS.frames}`).each(function () {
      this.style.height = `${height}px`;
    });
  }

  function activateHomeTab() {
    const $home = $(SELECTORS.homeTabLink);
    if ($home.length) {
      $home.tab('show');
      updateDocumentTitle('Workspace');
    }
  }

  function updateDocumentTitle(tabTitle) {
    const baseTitle = 'SPK Sistemas';
    if (!tabTitle || tabTitle.trim() === '' || tabTitle === 'Workspace') {
      document.title = baseTitle;
      return;
    }
    document.title = `${tabTitle} | ${baseTitle}`;
  }

  function buildTabMarkup(tabId, fullUrl, title, icon) {
    return $(`
      <li class="nav-item spk-workspace-tab" data-tab-url="${fullUrl}" data-tab-id="${tabId}">
        <a class="nav-link" data-toggle="tab" href="#${tabId}" role="tab" aria-controls="${tabId}" aria-selected="false">
          <i class="${icon} mr-1"></i>
          <span class="tab-title">${title}</span>
          <button type="button" class="btn btn-sm btn-link px-1 ml-1 text-muted reload-tab" title="Recarregar aba">
            <i class="fas fa-sync-alt"></i>
          </button>
          <button type="button" class="btn btn-sm btn-link px-1 text-muted close-tab" title="Fechar aba">
            <i class="fas fa-times"></i>
          </button>
        </a>
      </li>
    `);
  }

  function buildPaneMarkup(tabId, fullUrl, title) {
    return $(`
      <div class="tab-pane fade spk-workspace-pane" id="${tabId}" role="tabpanel" aria-label="${title}">
        <div class="p-0">
          <iframe class="workspace-frame border-0 w-100"
                  src="${fullUrl}"
                  loading="lazy"
                  referrerpolicy="no-referrer-when-downgrade"
                  sandbox="allow-same-origin allow-forms allow-scripts allow-modals allow-downloads">
          </iframe>
        </div>
      </div>
    `);
  }

  function getDefaultTitleFromUrl(fullUrl) {
    try {
      const parsed = new URL(fullUrl);
      const path = (parsed.pathname || '').replace(/^\/+|\/+$/g, '');
      if (!path) return 'Aba';
      return path
        .split('/')
        .filter(Boolean)
        .map(part => part.charAt(0).toUpperCase() + part.slice(1))
        .join(' / ');
    } catch (e) {
      return 'Aba';
    }
  }

  function openInWorkspace(url, title, icon) {
    if (!url) return;

    const fullUrl = toAbsoluteUrl(url);
    if (!fullUrl) return;

    const tabId = hash(fullUrl);
    const $tabs = $(SELECTORS.tabs);
    const $content = $(SELECTORS.content);

    if (!$tabs.length || !$content.length) {
      window.location.href = fullUrl;
      return;
    }

    const $existingLink = $tabs.find(`a[href="#${tabId}"]`);
    if ($existingLink.length) {
      $existingLink.tab('show');
      updateDocumentTitle(title || $existingLink.find('.tab-title').text().trim());
      adjustIframesHeight();
      return;
    }

    const finalTitle = title || getDefaultTitleFromUrl(fullUrl);
    const finalIcon = icon || 'fas fa-window-maximize';

    const $tab = buildTabMarkup(tabId, fullUrl, finalTitle, finalIcon);
    const $pane = buildPaneMarkup(tabId, fullUrl, finalTitle);

    $tabs.append($tab);
    $content.append($pane);

    $tab.find('.close-tab').on('click', function (e) {
      e.preventDefault();
      e.stopPropagation();

      const wasActive = $tab.find('a.nav-link').hasClass('active');

      $pane.remove();
      $tab.remove();

      if (wasActive) {
        const $lastOpenTab = $tabs.find('a.nav-link').last();
        if ($lastOpenTab.length) {
          $lastOpenTab.tab('show');
          const lastTitle = $lastOpenTab.find('.tab-title').text().trim();
          updateDocumentTitle(lastTitle || 'Workspace');
        } else {
          activateHomeTab();
        }
      }

      adjustIframesHeight();
    });

    $tab.find('.reload-tab').on('click', function (e) {
      e.preventDefault();
      e.stopPropagation();

      const $frame = $pane.find('iframe.workspace-frame');
      const currentSrc = $frame.attr('src');
      $frame.attr('src', currentSrc);
    });

    $tab.find('a.nav-link').on('shown.bs.tab', function () {
      updateDocumentTitle(finalTitle);
      adjustIframesHeight();
    });

    $tab.find('a.nav-link').tab('show');
    adjustIframesHeight();
  }

  function bindWorkspaceLinks() {
    $(document).on('click', 'a[data-workspace]', function (e) {
      if (e.which !== 1 || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) {
        return;
      }

      const $link = $(this);
      const url = $link.attr('href');

      if (!url || url === '#') {
        return;
      }

      e.preventDefault();

      const title = $link.data('title');
      const icon = $link.data('icon');

      openInWorkspace(url, title, icon);
    });
  }

  function bindHomeTabEvents() {
    $(document).on('shown.bs.tab', SELECTORS.homeTabLink, function () {
      updateDocumentTitle('Workspace');
      adjustIframesHeight();
    });
  }

  function init() {
    window.openInWorkspace = openInWorkspace;

    bindWorkspaceLinks();
    bindHomeTabEvents();

    $(window).on('resize', adjustIframesHeight);

    $(document).ready(function () {
      adjustIframesHeight();
      updateDocumentTitle('Workspace');
    });
  }

  init();
})();