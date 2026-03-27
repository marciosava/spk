/*!
 * =========================================================
 * SPK SISTEMAS - FILTROS PADRÃO
 * Controle global de accordion de filtros das grids
 * =========================================================
 */
(function (window, document) {
    'use strict';

    const SELECTORS = {
        container: '[data-spk-filter]',
        body: '[data-spk-filter-body]',
        toggle: '[data-spk-filter-toggle]',
        icon: '[data-spk-filter-icon]',
        btnPesquisar: '[data-spk-filter-search]',
        btnLimpar: '[data-spk-filter-clear]'
    };

    function hasJQueryCollapse(element) {
        return !!(window.jQuery && element && window.jQuery.fn && window.jQuery.fn.collapse);
    }

    function getElements(container) {
        if (!container) {
            return {};
        }

        return {
            container: container,
            body: container.querySelector(SELECTORS.body),
            toggle: container.querySelector(SELECTORS.toggle),
            icon: container.querySelector(SELECTORS.icon),
            btnPesquisar: container.querySelector(SELECTORS.btnPesquisar),
            btnLimpar: container.querySelector(SELECTORS.btnLimpar)
        };
    }

    function isExpanded(body) {
        return !!(body && body.classList.contains('show'));
    }

    function updateIcon(elements) {
        if (!elements || !elements.body || !elements.icon) {
            return;
        }

        const expanded = isExpanded(elements.body);
        elements.icon.textContent = expanded ? '−' : '+';

        if (elements.toggle) {
            elements.toggle.setAttribute('aria-expanded', expanded ? 'true' : 'false');
        }
    }

    function show(elements) {
        if (!elements || !elements.body) {
            return;
        }

        if (hasJQueryCollapse(elements.body)) {
            window.jQuery(elements.body).collapse('show');
        } else {
            elements.body.classList.add('show');
            updateIcon(elements);
        }
    }

    function hide(elements) {
        if (!elements || !elements.body) {
            return;
        }

        if (hasJQueryCollapse(elements.body)) {
            window.jQuery(elements.body).collapse('hide');
        } else {
            elements.body.classList.remove('show');
            updateIcon(elements);
        }
    }

    function clearFields(container) {
        if (!container) {
            return;
        }

        const fields = container.querySelectorAll('input, select, textarea');

        fields.forEach(function (field) {
            const tag = field.tagName.toLowerCase();
            const type = (field.type || '').toLowerCase();

            if (tag === 'button') {
                return;
            }

            if (type === 'hidden' || field.hasAttribute('data-spk-no-clear')) {
                return;
            }

            if (type === 'checkbox' || type === 'radio') {
                field.checked = false;
                return;
            }

            if (tag === 'select') {
                field.selectedIndex = 0;
                field.dispatchEvent(new Event('change', { bubbles: true }));
                return;
            }

            field.value = '';
        });
    }

    function dispatchFilterEvent(container, eventName, detail) {
        if (!container) {
            return;
        }

        container.dispatchEvent(new CustomEvent(eventName, {
            bubbles: true,
            detail: detail || {}
        }));
    }

    function bindCollapseEvents(elements) {
        if (!window.jQuery || !elements || !elements.body) {
            return;
        }

        const $body = window.jQuery(elements.body);

        $body.off('.spkFilter');

        $body.on('shown.bs.collapse.spkFilter', function () {
            updateIcon(elements);
        });

        $body.on('hidden.bs.collapse.spkFilter', function () {
            updateIcon(elements);
        });
    }

    function bindToggleFallback(elements) {
        if (!elements || !elements.toggle) {
            return;
        }

        elements.toggle.addEventListener('click', function () {
            window.setTimeout(function () {
                updateIcon(elements);
            }, 20);

            window.setTimeout(function () {
                updateIcon(elements);
            }, 350);
        });
    }

    function bindContainer(container) {
        if (!container || container.dataset.spkFilterBound === 'true') {
            return;
        }

        const elements = getElements(container);
        if (!elements.body) {
            return;
        }

        container.dataset.spkFilterBound = 'true';

        if (hasJQueryCollapse(elements.body)) {
            bindCollapseEvents(elements);
        }

        bindToggleFallback(elements);

        if (elements.btnPesquisar) {
            elements.btnPesquisar.addEventListener('click', function () {
                dispatchFilterEvent(container, 'spk:filtro:pesquisar', {
                    container: container,
                    elements: elements
                });
            });
        }

        if (elements.btnLimpar) {
            elements.btnLimpar.addEventListener('click', function () {
                clearFields(container);
                show(elements);

                dispatchFilterEvent(container, 'spk:filtro:limpar', {
                    container: container,
                    elements: elements
                });
            });
        }

        updateIcon(elements);

        container.spkFilter = {
            elements: elements,
            show: function () { show(elements); },
            hide: function () { hide(elements); },
            updateIcon: function () { updateIcon(elements); },
            clearFields: function () { clearFields(container); }
        };
    }

    function init(root) {
        const scope = root || document;
        const containers = scope.querySelectorAll(SELECTORS.container);

        containers.forEach(function (container) {
            bindContainer(container);
        });
    }

    window.SpkFiltros = {
        init: init
    };

    document.addEventListener('DOMContentLoaded', function () {
        init(document);
    });

})(window, document);