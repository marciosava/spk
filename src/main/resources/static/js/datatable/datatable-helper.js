// =============================================
// DATA TABLES HELPER - Unificado para o projeto
// =============================================

/**
 * Configuração genérica para DataTables
 * @param {string} tableId - ID da tabela HTML
 * @param {object} options - Opções personalizadas (opcional)
 */
function DataTableHelper(tableId, options = {}) {

    // Idioma PT-BR
    const portugueseLanguage = {
        "decimal": ",",
        "thousands": ".",
        "sEmptyTable": "Nenhum registro encontrado",
        "sInfo": "Mostrando _START_ até _END_ de _TOTAL_ registros",
        "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
        "sInfoFiltered": "(Filtrados de _MAX_ registros)",
        "sLengthMenu": "Mostrar _MENU_ registros por página",
        "sSearch": "Pesquisar:",
        "oPaginate": {
            "sFirst": "Primeiro",
            "sPrevious": "Anterior",
            "sNext": "Próximo",
            "sLast": "Último"
        },
        "select": {
            "rows": {
                "_": "%d linhas selecionadas",
                "0": "Nenhuma linha selecionada",
                "1": "1 linha selecionada"
            }
        },
        "buttons": {
            "copy": "Copiar",
            "copyTitle": "Copiado para a área de transferência",
            "copySuccess": {
                "_": "%d linhas copiadas",
                "1": "1 linha copiada"
            },
            "print": "Imprimir",
            "pageLength": "Mostrar %d registros"
        }
    };

    // Configurações padrão
    const defaults = {
        dom:
            '<"row mb-2"<"col-md-6"B><"col-md-6"f>>' + // Botões + Filtro
            '<"table-responsive"t>' +                 // Tabela responsiva
            '<"row mt-2"<"col-md-6"i><"col-md-6"p>>', // Info + Paginação

        language: portugueseLanguage,
        responsive: true,
        lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Todos"]],
        pageLength: 10,
        autoWidth: false,

        buttons: [
            {
                extend: 'excel',
                text: '<i class="fas fa-file-excel"></i> Excel',
                className: 'btn btn-sm btn-outline-success',
                exportOptions: { columns: ':not(.no-export)' }
            },
            {
                extend: 'print',
                text: '<i class="fas fa-print"></i> Imprimir',
                className: 'btn btn-sm btn-outline-secondary',
                exportOptions: { columns: ':not(.no-export)' }
            }
        ]
    };

    // Merge de configurações
    const config = $.extend(true, {}, defaults, options);

    // Se já existir tabela inicializada, destruir antes
    if ($.fn.DataTable.isDataTable('#' + tableId)) {
        $('#' + tableId).DataTable().destroy();
    }

    // Inicializa DataTable
    return $('#' + tableId).DataTable(config);
}

// Inicialização automática para tabelas com a classe 'datatable'
$(document).ready(function () {
    $('table.datatable').not('[data-no-auto-init]').each(function () {
        const tableId = $(this).attr('id') || 'datatable-' + Math.floor(Math.random() * 1000);
        $(this).attr('id', tableId);
        DataTableHelper(tableId);
    });
});
