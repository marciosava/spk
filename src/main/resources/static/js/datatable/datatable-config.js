(function (window, $) {
    'use strict';

    if (!$) {
        console.error('SPK DataTable: jQuery não foi carregado.');
        return;
    }

    const SPKDataTable = {
        language: {
            decimal: ",",
            emptyTable: "Nenhum registro encontrado",
            info: "Mostrando de _START_ até _END_ de _TOTAL_ registros",
            infoEmpty: "Mostrando 0 até 0 de 0 registros",
            infoFiltered: "(filtrado de _MAX_ registros no total)",
            infoPostFix: "",
            thousands: ".",
            lengthMenu: "Mostrar _MENU_ registros",
            loadingRecords: "Carregando...",
            processing: "Processando...",
            search: "Pesquisar:",
            zeroRecords: "Nenhum registro encontrado",
            paginate: {
                first: "Primeiro",
                last: "Último",
                next: "Próximo",
                previous: "Anterior"
            },
            aria: {
                sortAscending: ": ativar para ordenar a coluna de forma crescente",
                sortDescending: ": ativar para ordenar a coluna de forma decrescente"
            }
        },

        defaultDom: "<'row mb-2'<'col-sm-12 col-md-6'l><'col-sm-12 col-md-6'f>>" +
                    "<'row'<'col-sm-12'tr>>" +
                    "<'row mt-2'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",

        buildDefaultConfig: function (options) {
            const config = {
                processing: true,
                serverSide: true,
                responsive: true,
                autoWidth: false,
                searching: false,
                lengthChange: true,
                pageLength: 10,
                ordering: true,
                language: this.language,
                dom: this.defaultDom,
                ajax: {
                    url: options.ajaxUrl,
                    type: 'GET',
                    data: function (d) {
                        if (typeof options.ajaxData === 'function') {
                            options.ajaxData(d);
                        }
                    },
                    dataSrc: function (json) {
                        return json && json.data ? json.data : [];
                    },
                    error: function (xhr, status, error) {
                        console.error('Erro ao carregar DataTable:', error);
                        console.error(xhr);
                    }
                },
                columns: options.columns || [],
                order: options.order || [[0, 'asc']],
                drawCallback: function () {
                    SPKDataTable.bindRowActive();
                    SPKDataTable.enableTooltips();
                }
            };

            if (options.extraConfig && typeof options.extraConfig === 'object') {
                $.extend(true, config, options.extraConfig);
            }

            return config;
        },

        init: function (selector, options) {
            if (!selector) {
                console.error('SPK DataTable: seletor não informado.');
                return null;
            }

            if (!options || !options.ajaxUrl) {
                console.error('SPK DataTable: ajaxUrl não informado.');
                return null;
            }

            const $table = $(selector);

            if (!$table.length) {
                console.error('SPK DataTable: tabela não encontrada para o seletor:', selector);
                return null;
            }

            if (!$.fn.DataTable) {
                console.error('SPK DataTable: plugin DataTables não foi carregado.');
                return null;
            }

            const config = this.buildDefaultConfig(options);
            return $table.DataTable(config);
        },

        reload: function (table) {
            if (table && table.ajax) {
                table.ajax.reload(null, false);
            }
        },

        bindSearchForm: function (table, config) {
            if (!table || !config) {
                return;
            }

            if (config.searchButton) {
                $(document).off('click.spkSearch', config.searchButton);
                $(document).on('click.spkSearch', config.searchButton, function (e) {
                    e.preventDefault();
                    SPKDataTable.reload(table);
                });
            }

            if (config.clearButton) {
                $(document).off('click.spkClear', config.clearButton);
                $(document).on('click.spkClear', config.clearButton, function (e) {
                    e.preventDefault();

                    if (config.formSelector) {
                        const $form = $(config.formSelector);
                        if ($form.length) {
                            $form[0].reset();
                        }
                    }

                    if (Array.isArray(config.clearFields)) {
                        config.clearFields.forEach(function (selector) {
                            $(selector).val('');
                        });
                    }

                    SPKDataTable.reload(table);
                });
            }

            if (Array.isArray(config.enterSubmitFields)) {
                config.enterSubmitFields.forEach(function (selector) {
                    $(document).off('keypress.spkEnter', selector);
                    $(document).on('keypress.spkEnter', selector, function (e) {
                        if (e.which === 13) {
                            e.preventDefault();
                            SPKDataTable.reload(table);
                        }
                    });
                });
            }
        },

        renderBadgeStatus: function (ativo) {
            if (ativo === true || ativo === 'true' || ativo === 'Ativo' || ativo === 1 || ativo === '1') {
                return '<span class="badge badge-success">Ativo</span>';
            }
            return '<span class="badge badge-secondary">Inativo</span>';
        },

        renderPerfis: function (roles) {
            if (!roles || String(roles).trim() === '') {
                return '<span class="text-muted">Não informado</span>';
            }

            return String(roles)
                .split(',')
                .map(function (role) {
                    const roleTrim = role.trim();
                    const roleLabel = roleTrim.replace('ROLE_', '').replace(/_/g, ' ');
                    return '<span class="badge badge-info mr-1 mb-1">' +
                        SPKDataTable.escapeHtml(roleLabel) +
                        '</span>';
                })
                .join('');
        },

        renderAcoesPadrao: function (config) {
            const acoes = [];

            if (config.visualizar) {
                acoes.push(`
                    <a href="${config.visualizar}"
                       class="spk-btn-acao spk-btn-acao--view"
                       title="Visualizar">
                        <i class="fas fa-eye"></i>
                    </a>
                `);
            }

            if (config.editar) {
                acoes.push(`
                    <a href="${config.editar}"
                       class="spk-btn-acao spk-btn-acao--edit"
                       title="Editar">
                        <i class="fas fa-pen"></i>
                    </a>
                `);
            }

            if (config.excluir) {
                acoes.push(`
                    <button type="button"
                            class="spk-btn-acao spk-btn-acao--delete btn-excluir"
                            data-id="${config.id}"
                            data-nome="${SPKDataTable.escapeHtml(config.nome || '')}"
                            title="Excluir">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                `);
            }

            return `<div class="spk-acoes">${acoes.join('')}</div>`;
        },

        bindRowActive: function () {
            $(document).off('click.spkRowActive', '.table tbody tr');
            $(document).on('click.spkRowActive', '.table tbody tr', function () {
                const $tbody = $(this).closest('tbody');
                $tbody.find('tr').removeClass('spk-row-active');
                $(this).addClass('spk-row-active');
            });
        },

        enableTooltips: function () {
            if ($.fn.tooltip) {
                $('[title]').tooltip('dispose').tooltip({
                    trigger: 'hover'
                });
            }
        },

        escapeHtml: function (value) {
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
    };

    window.SPKDataTable = SPKDataTable;

})(window, window.jQuery);