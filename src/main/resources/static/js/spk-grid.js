/*!
 * =========================================================
 * SPK SISTEMAS - GRID PADRÃO
 * Integração global de DataTables + filtros SPK
 * =========================================================
 */
(function (window, document) {
    'use strict';

    function debounce(fn, delay) {
        let timer = null;

        return function () {
            const context = this;
            const args = arguments;

            clearTimeout(timer);
            timer = setTimeout(function () {
                fn.apply(context, args);
            }, delay || 400);
        };
    }

    function escapeHtml(value) {
        if (value === null || value === undefined) {
            return '';
        }

        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function defaultLanguage(emptyMessage) {
        return {
            processing: 'Processando...',
            search: 'Pesquisar:',
            lengthMenu: 'Mostrar _MENU_ registros',
            info: 'Mostrando de _START_ até _END_ de _TOTAL_ registros',
            infoEmpty: 'Mostrando 0 até 0 de 0 registros',
            infoFiltered: '(filtrado de _MAX_ registros no total)',
            loadingRecords: 'Carregando...',
            zeroRecords: emptyMessage || 'Nenhum registro encontrado',
            emptyTable: emptyMessage || 'Nenhum registro disponível',
            paginate: {
                first: 'Primeiro',
                previous: 'Anterior',
                next: 'Próximo',
                last: 'Último'
            },
            aria: {
                sortAscending: ': ativar para ordenar a coluna em ordem crescente',
                sortDescending: ': ativar para ordenar a coluna em ordem decrescente'
            }
        };
    }

    function getFilterValue(selector) {
        if (!selector) {
            return '';
        }

        const element = document.querySelector(selector);
        if (!element) {
            return '';
        }

        if (element.type === 'checkbox') {
            return element.checked;
        }

        return element.value || '';
    }

    function setInitialMessage(tableSelector, columnCount, message) {
        const table = document.querySelector(tableSelector);
        if (!table) {
            return;
        }

        const tbody = table.querySelector('tbody');
        if (!tbody) {
            return;
        }

        const totalCols = columnCount || table.querySelectorAll('thead th').length || 1;

        tbody.innerHTML = ''
            + '<tr class="spk-grid-placeholder-row">'
            + '    <td colspan="' + totalCols + '" class="text-center text-muted py-4">'
            +          escapeHtml(message || 'Informe os filtros desejados e clique em Pesquisar.')
            + '    </td>'
            + '</tr>';
    }

    function clearTableData(dt) {
        if (!dt) {
            return;
        }

        dt.clear().draw();
    }

    function bindLiveFilters(api, filters, delay) {
        if (!filters || !filters.length) {
            return;
        }

        const reload = debounce(function () {
            if (!api.hasSearched()) {
                return;
            }
            api.reload();
        }, delay || 400);

        filters.forEach(function (selector) {
            const field = document.querySelector(selector);
            if (!field) {
                return;
            }

            const tag = (field.tagName || '').toLowerCase();

            if (tag === 'select' || field.type === 'checkbox') {
                field.addEventListener('change', function () {
                    if (!api.hasSearched()) {
                        return;
                    }
                    api.reload();
                });
                return;
            }

            field.addEventListener('input', reload);
        });
    }

    function bindFilterEvents(api, filterContainerSelector) {
        if (!filterContainerSelector) {
            return;
        }

        const container = document.querySelector(filterContainerSelector);
        if (!container) {
            return;
        }

        container.addEventListener('spk:filtro:pesquisar', function () {
            api.search();
        });

        container.addEventListener('spk:filtro:limpar', function () {
            api.reset();
        });
    }

    function bindEnterReload(api, filters) {
        if (!filters || !filters.length) {
            return;
        }

        filters.forEach(function (selector) {
            const field = document.querySelector(selector);
            if (!field) {
                return;
            }

            field.addEventListener('keypress', function (e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    api.search();
                }
            });
        });
    }

    function createDataTable(options) {
        if (!window.jQuery || !window.jQuery.fn || !window.jQuery.fn.DataTable) {
            console.error('SPK Grid: DataTables não está carregado.');
            return null;
        }

        if (!options || !options.tableSelector || !options.ajaxUrl) {
            console.error('SPK Grid: parâmetros obrigatórios não informados.');
            return null;
        }

        const $ = window.jQuery;
        const $table = $(options.tableSelector);

        if (!$table.length) {
            console.error('SPK Grid: tabela não encontrada:', options.tableSelector);
            return null;
        }

        const autoLoad = options.autoLoad !== false;
        const initialMessage = options.initialMessage || 'Informe os filtros desejados e clique em Pesquisar.';
        const columnCount = (options.columns && options.columns.length) || $table.find('thead th').length || 1;

        let hasSearched = autoLoad;

        const dt = $table.DataTable({
            processing: options.processing !== false,
            serverSide: options.serverSide !== false,
            searching: options.searching === true,
            paging: options.paging !== false,
            ordering: options.ordering !== false,
            responsive: options.responsive === true,
            autoWidth: false,
            deferLoading: autoLoad ? null : 0,
            pageLength: options.pageLength || 10,
            lengthMenu: options.lengthMenu || [10, 25, 50, 100],
            order: options.order || [],
            language: options.language || defaultLanguage(
                autoLoad ? 'Nenhum registro encontrado' : initialMessage
            ),
            ajax: {
                url: options.ajaxUrl,
                type: options.ajaxMethod || 'GET',
                data: function (d) {
                    if (!hasSearched && !autoLoad) {
                        d._spk_no_load = true;
                    }

                    if (options.extraData && typeof options.extraData === 'function') {
                        const customData = options.extraData();
                        if (customData && typeof customData === 'object') {
                            Object.keys(customData).forEach(function (key) {
                                d[key] = customData[key];
                            });
                        }
                    }

                    if (options.filters && Array.isArray(options.filters)) {
                        options.filters.forEach(function (item) {
                            d[item.param] = getFilterValue(item.selector);
                        });
                    }
                },
                error: function () {
                    if (typeof options.onAjaxError === 'function') {
                        options.onAjaxError();
                    }
                }
            },
            columns: options.columns || [],
            drawCallback: function (settings) {
                if (!hasSearched && !autoLoad) {
                    setInitialMessage(options.tableSelector, columnCount, initialMessage);
                }

                if (typeof options.drawCallback === 'function') {
                    options.drawCallback(settings);
                }
            },
            initComplete: function (settings, json) {
                if (!autoLoad) {
                    clearTableData(this.api());
                    setInitialMessage(options.tableSelector, columnCount, initialMessage);
                }

                if (typeof options.initComplete === 'function') {
                    options.initComplete(settings, json);
                }
            }
        });

        const api = {
            dt: dt,
            search: function () {
                hasSearched = true;
                dt.ajax.reload();
            },
            reload: function () {
                if (!hasSearched && !autoLoad) {
                    return;
                }
                dt.ajax.reload();
            },
            reset: function () {
                hasSearched = false;
                clearTableData(dt);
                setInitialMessage(options.tableSelector, columnCount, initialMessage);
            },
            hasSearched: function () {
                return hasSearched;
            }
        };

        bindFilterEvents(api, options.filterContainerSelector);

        if (options.liveFilters && options.liveFilters.length) {
            bindLiveFilters(api, options.liveFilters, options.liveSearchDelay || 400);
        }

        if (options.enterFilters && options.enterFilters.length) {
            bindEnterReload(api, options.enterFilters);
        }

        return api;
    }

    window.SpkGrid = {
        create: createDataTable,
        escapeHtml: escapeHtml
    };

})(window, document);