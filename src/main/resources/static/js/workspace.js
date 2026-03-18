/* ========= Workspace Manager (tabs + iframes) ========= */
/*
  v2.2 — 2025-10-06
  Alterações:
  ✅ URLs absolutas (corrige erro "localhost se recusou a se conectar")
  ✅ Isolamento seguro do iframe (referrerpolicy e sandbox)
  ✅ Altura automática robusta
  ✅ Proteção contra múltiplas tabs idênticas
  ✅ Recarregar/Fechar 100% seguros
*/

(function () {

  /* --- Utilitário: hash estável da URL para ID da aba --- */
  function hash(str) {
    let h = 0;
    for (let i = 0; i < str.length; i++) {
      h = (h << 5) - h + str.charCodeAt(i);
      h |= 0;
    }
    return 'tab_' + Math.abs(h);
  }

  /* --- Ajuste dinâmico da altura dos iframes --- */
  function adjustIframesHeight() {
    const $content = $('#workspaceContent');
    const offset = $content.offset();
    const top = offset ? offset.top : 0;
    const vh = window.innerHeight || document.documentElement.clientHeight;
    const h = Math.max(300, vh - top - 20); // mínimo 300px
    $('#workspaceContent iframe.workspace-frame').each(function () {
      this.style.height = h + 'px';
    });
  }

  /* --- Cria ou ativa uma aba no workspace --- */
  function openInWorkspace(url, title, icon) {
    if (!url) return;

    /* --- Garante URL absoluta (http://localhost:8080/usuarios ...) --- */
    let fullUrl;
    try {
      fullUrl = new URL(url, window.location.origin).toString();
    } catch (e) {
      fullUrl = window.location.origin + (url.startsWith('/') ? url : '/' + url);
    }

    const tabId = hash(fullUrl);
    const $tabs = $('#workspaceTabs');
    const $content = $('#workspaceContent');

    /* --- Evita duplicar abas --- */
    const existing = $tabs.find(`a[href="#${tabId}"]`);
    if (existing.length) {
      existing.tab('show');
      adjustIframesHeight();
      return;
    }

    /* --- Título e ícone padrão --- */
    if (!title) {
      try {
        const u = new URL(fullUrl);
        title = (u.pathname || '/').replace(/\/+/g, ' ').trim() || 'Aba';
      } catch {
        title = 'Aba';
      }
    }
    icon = icon || 'fas fa-window-maximize';

    /* --- Cria aba (li/nav-link) --- */
    const $li = $(`
      <li class="nav-item" data-tab-url="${fullUrl}">
        <a class="nav-link" data-toggle="tab" href="#${tabId}" role="tab" aria-controls="${tabId}">
          <i class="${icon} mr-1"></i>
          <span class="tab-title">${title}</span>
          <button type="button" class="btn btn-sm btn-link px-1 ml-1 text-muted reload-tab" title="Recarregar">
            <i class="fas fa-sync-alt"></i>
          </button>
          <button type="button" class="btn btn-sm btn-link px-1 text-muted close-tab" title="Fechar">
            <i class="fas fa-times"></i>
          </button>
        </a>
      </li>
    `);

    /* --- Cria conteúdo (iframe seguro) --- */
    const $pane = $(`
      <div class="tab-pane fade" id="${tabId}" role="tabpanel" aria-labelledby="${tabId}-tab">
        <div class="p-0">
          <iframe class="workspace-frame border-0 w-100"
                  src="${fullUrl}"
                  loading="lazy"
                  referrerpolicy="no-referrer-when-downgrade"
                  sandbox="allow-same-origin allow-forms allow-scripts allow-modals">
          </iframe>
        </div>
      </div>
    `);

    $tabs.append($li);
    $content.append($pane);

    /* --- Fechar aba --- */
    $li.find('.close-tab').on('click', function (e) {
      e.preventDefault();
      const isActive = $li.find('a').hasClass('active');
      $pane.remove();
      $li.remove();

      // se era a aba ativa, volta para home
      if (isActive) {
        const $last = $tabs.find('a.nav-link').last();
        ($last.length ? $last : $('#tab-home-link')).tab('show');
      }
      adjustIframesHeight();
    });

    /* --- Recarregar aba --- */
    $li.find('.reload-tab').on('click', function (e) {
      e.preventDefault();
      const $frame = $pane.find('iframe.workspace-frame');
      $frame.attr('src', $frame.attr('src')); // reload seguro
    });

    /* --- Ativa a aba recém-criada --- */
    $li.find('a').tab('show');
    adjustIframesHeight();
  }

  /* --- Intercepta links com data-workspace --- */
  $(document).on('click', 'a[data-workspace]', function (e) {
    if (e.which === 1 && !e.ctrlKey && !e.metaKey) {
      e.preventDefault();
      const $a = $(this);
      const url = $a.attr('href');
      const title = $a.data('title');
      const icon = $a.data('icon');
      openInWorkspace(url, title, icon);
    }
  });

  /* --- API global --- */
  window.openInWorkspace = openInWorkspace;

  /* --- Ajusta altura quando redimensiona ou carrega --- */
  $(window).on('resize', adjustIframesHeight);
  $(document).ready(adjustIframesHeight);

  /* --- Modo legado (para páginas antigas com layout:fragment="corpo") --- */
  $(function () {
    const $slot = $('#legacy-slot');
    if ($slot.find('[layout\\:fragment="corpo"]').length > 0) {
      $slot.show();
    }
  });

})();
