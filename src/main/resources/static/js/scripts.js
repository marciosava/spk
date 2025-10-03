///**
// * 	Script Customizado novo projeto baseado adminLTE scripts.ja

//// =============================================
//// CONTROLE DO SIDEBAR RECOLHÍVEL
//// =============================================
const path = window.location.pathname;
const navLinks = document.querySelectorAll('.nav-link');

navLinks.forEach(link => {
    if (link.href.includes(path)) {
        link.classList.add('active');

        const treeview = link.closest('.has-treeview');
        if (treeview) {
            treeview.classList.add('menu-open');
            const parentLink = treeview.querySelector('.nav-link');
            if (parentLink) {
                parentLink.classList.add('active');
            }
        }
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const breadcrumb = document.getElementById('breadcrumb-dinamico');
    const tituloPagina = document.getElementById('titulo-pagina');

    if (breadcrumb && tituloPagina) {
        const path = window.location.pathname;
        const partes = path.split('/').filter(Boolean); // remove strings vazias

        // Primeira parte é sempre 'Home'
        const lista = [];
        lista.push(`<li class="breadcrumb-item"><a href="/dashboard">Home</a></li>`);

        for (let i = 0; i < partes.length; i++) {
            const nome = partes[i].charAt(0).toUpperCase() + partes[i].slice(1);
            const link = '/' + partes.slice(0, i + 1).join('/');
            if (i === partes.length - 1) {
                lista.push(`<li class="breadcrumb-item active">${nome}</li>`);
                tituloPagina.textContent = nome;
            } else {
                lista.push(`<li class="breadcrumb-item"><a href="${link}">${nome}</a></li>`);
            }
        }

        breadcrumb.innerHTML = lista.join('');
    }
});


// */
//// scripts.js
//
//// =============================================
//// CONTROLE DE TEMA (menu principal)
//// =============================================
//
//function applyTheme(theme) {
//    document.body.setAttribute('data-theme', theme);
//    localStorage.setItem('theme', theme);
//}
//
//function toggleTheme() {
//    const current = document.body.getAttribute('data-theme') || 'light';
//    const newTheme = current === 'dark' ? 'light' : 'dark';
//    applyTheme(newTheme);
//}
//
//// Aplica o tema salvo ao carregar
//(function() {
//    const savedTheme = localStorage.getItem('theme') || 'light';
//    applyTheme(savedTheme);
//})();
//
//// =============================================
//// DATA TABLES HELPER (Nova funcionalidade)
//// =============================================
//
//
//// =============================================
////
////
////   Estes ainda nao estao sendo usados, estao aqui como exemplo.
////   Goiania-GO, 10-06-2025
////
//// =============================================
//// FUNÇÕES GERAIS (jQuery)
//// =============================================
//
//$(document).ready(function() {
//    // Mensagens com timeout
//    setTimeout(function() {
//        $(".alert").fadeOut("slow", function() {
//            $(this).alert('close');
//        });
//    }, 15000);
//    
//    // Máscaras de campos
//    $('#valorMonetario').mask('000.000.000,00', {reverse: true});
//    $('#celular').mask('(99) 99999-9999');
//    $('#telefone').mask('(99) 9999-9999');
//    $('#cep').mask('99999-999');
//    
//    // Controle do sidebar
//    $('[data-toggle="sidebar"]').click(function() {
//        $('.sidebar').toggleClass('collapse');
//    });
//});
//
//// =============================================
//// CONFIRMAÇÃO DE EXCLUSÃO
//// =============================================
//
//function confirmarExclusaoGenerico(url) {
//    Swal.fire({
//        title: 'Tem certeza?',
//        text: 'Esta ação não poderá ser desfeita!',
//        icon: 'warning',
//        showCancelButton: true,
//        confirmButtonColor: '#d33',
//        cancelButtonColor: '#6c757d',
//        confirmButtonText: 'Sim, excluir',
//        cancelButtonText: 'Cancelar'
//    }).then((result) => {
//        if (result.isConfirmed) {
//            window.location.href = url;
//        }
//    });
//}
//
//document.addEventListener('DOMContentLoaded', function() {
//    document.querySelectorAll('.btn-excluir').forEach(function(botao) {
//        botao.addEventListener('click', function() {
//            const url = this.getAttribute('data-url');
//            confirmarExclusaoGenerico(url);
//        });
//    });
//});
//
//// Adicione este event listener no DOMContentLoaded
//document.addEventListener('DOMContentLoaded', function() {
//    // Delegation para o botão de status
//    document.addEventListener('click', function(e) {
//        if (e.target.closest('.status-toggle')) {
//            e.preventDefault();
//            const button = e.target.closest('.status-toggle');
//            const url = button.getAttribute('data-url');
//            const estaAtivo = button.getAttribute('data-ativo') === 'true';
//            
//            confirmarAtivacaoDesativacao(url, estaAtivo);
//        }
//    });
//});
//
//function confirmarAtivacaoDesativacao(url, estaAtivo) {
//    const acao = estaAtivo ? 'desativar' : 'ativar';
//    const icon = estaAtivo ? 'warning' : 'success';
//    
//    Swal.fire({
//        title: `Confirmar ${acao} usuário?`,
//        text: `Deseja realmente ${acao} este usuário?`,
//        icon: icon,
//        showCancelButton: true,
//        confirmButtonColor: estaAtivo ? '#d33' : '#28a745',
//        cancelButtonColor: '#6c757d',
//        confirmButtonText: `Sim, ${acao}!`,
//        cancelButtonText: 'Cancelar',
//        reverseButtons: true
//    }).then((result) => {
//        if (result.isConfirmed) {
//            // Adiciona feedback visual durante a requisição
//            Swal.fire({
//                title: 'Processando...',
//                allowOutsideClick: false,
//                didOpen: () => {
//                    Swal.showLoading();
//                }
//            });
//            
//            // Faz a requisição AJAX
//            fetch(url, {
//                method: 'GET',
//                headers: {
//                    'Accept': 'application/json',
//                    'X-Requested-With': 'XMLHttpRequest'
//                }
//            })
//            .then(response => {
//                Swal.close();
//                if (response.ok) {
//                    // Recarrega apenas a página se tudo estiver OK
//                    window.location.reload();
//                } else {
//                    throw new Error('Erro na resposta do servidor');
//                }
//            })
//            .catch(error => {
//                Swal.fire('Erro!', 'Falha ao atualizar o status do usuário.', 'error');
//                console.error('Erro:', error);
//            });
//        }
//    });
//}
//
//// =============================================
//// DATA TABLES HELPER (Nova funcionalidade)
//// =============================================
//
///**
// * Configuração genérica para DataTables
// * @param {string} tableId - ID da tabela HTML
// * @param {object} options - Opções personalizadas (opcional)
// */
//function DataTableHelper(tableId, options = {}) {
//    // Configurações de linguagem em português
//	const portugueseLanguage = {
//	    "decimal": ",",
//	    "thousands": ".",
//	    "sEmptyTable": "Nenhum registro encontrado",
//	    "sInfo": "Mostrando _START_ até _END_ de _TOTAL_ registros",
//	    "sInfoEmpty": "Mostrando 0 até 0 de 0 registros",
//	    "sInfoFiltered": "(Filtrados de _MAX_ registros)",
//	    "sInfoPostFix": "",
//	    "sSearch": "Pesquisar:",
//	    "sUrl": "",
//	    "sLengthMenu": "Mostrar _MENU_ registros por página", // Esta é a configuração que faltava
//	    "oPaginate": {
//	        "sFirst": "Primeiro",
//	        "sPrevious": "Anterior",
//	        "sNext": "Próximo",
//	        "sLast": "Último"
//	    },
//	    "oAria": {
//	        "sSortAscending": ": ativar para ordenar coluna ascendente",
//	        "sSortDescending": ": ativar para ordenar coluna descendente"
//	    },
//	    "select": {
//	        "rows": {
//	            "_": "%d linhas selecionadas",
//	            "0": "Nenhuma linha selecionada",
//	            "1": "1 linha selecionada"
//	        }
//	    },
//	    "buttons": {
//	        "copy": "Copiar",
//	        "copyTitle": "Copiar para área de transferência",
//	        "copySuccess": {
//	            "_": "%d linhas copiadas",
//	            "1": "1 linha copiada"
//	        },
//	        "print": "Imprimir",
//	        "pageLength": "Mostrar %d registros"
//	    }
//	};
//
//
//    // Configurações padrão
//    const defaults = {
//        dom: '<"top"<"row"<"col-md-6"l><"col-md-6"f>>><"table-responsive"t><"bottom"<"row"<"col-md-6"i><"col-md-6"p>>>',
//        language: portugueseLanguage,
//        responsive: true,
//        lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "Todos"]],
//        pageLength: 10,
//        autoWidth: false,
//        // Configurações de botões padrão (podem ser sobrescritas)
//        buttons: {
//            dom: {
//                button: {
//                    className: 'btn btn-sm'
//                }
//            },
//            buttons: [
//                {
//                    extend: 'excel',
//                    text: '<i class="fas fa-file-excel"></i> Excel',
//                    className: 'btn-outline-success',
//                    title: '',
//                    exportOptions: {
//                        columns: ':not(.no-export)'
//                    }
//                },
//                {
//                    extend: 'print',
//                    text: '<i class="fas fa-print"></i> Imprimir',
//                    className: 'btn-outline-secondary',
//                    title: '',
//                    exportOptions: {
//                        columns: ':not(.no-export)'
//                    }
//                }
//            ]
//        }
//    };
//
//    // Mescla as opções padrão com as personalizadas (deep merge para buttons)
//    const config = {
//        ...defaults,
//        ...options,
//        buttons: {
//            ...defaults.buttons,
//            ...(options.buttons || {})
//        }
//    };
//
//    // Destrói a DataTable se já existir
//    if ($.fn.DataTable.isDataTable('#' + tableId)) {
//        $('#' + tableId).DataTable().destroy();
//    }
//
//    // Inicializa a DataTable
//    const table = $('#' + tableId).DataTable(config);
//
//    return table;
//}
//
//// Inicialização automática para tabelas com a classe 'datatable' (genérica)
//$(document).ready(function() {
//    $('table.datatable').not('[data-no-auto-init]').each(function() {
//        const tableId = $(this).attr('id') || 'datatable-' + Math.floor(Math.random() * 1000);
//        $(this).attr('id', tableId);
//        DataTableHelper(tableId);
//    });
//});
//
//
//// =============================================
//// CONTROLE DO SIDEBAR RECOLHÍVEL
//// =============================================
//document.addEventListener('DOMContentLoaded', function() {
//    const sidebar = document.querySelector('.navbar-collapsible');
//    const toggleBtn = document.querySelector('.sidebar-toggle');
//    const savedState = localStorage.getItem('sidebarCollapsed');
//
//    // Aplica estado salvo
//    if (savedState === 'true') {
//        sidebar.classList.add('collapsed');
//    }
//
//    // Configura o toggle
//    if (toggleBtn) {
//        toggleBtn.addEventListener('click', function() {
//            sidebar.classList.toggle('collapsed');
//            localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('collapsed'));
//            
//            // Atualiza o texto do botão
//            const toggleText = document.querySelector('.toggle-text');
//            if (toggleText) {
//                toggleText.textContent = sidebar.classList.contains('collapsed') ? 'Expandir' : 'Recolher';
//            }
//        });
//    }
//    
//    // Corrige margem inicial se necessário
//    if (sidebar.classList.contains('collapsed')) {
//        document.querySelector('.page-wrapper').style.marginLeft = '60px';
//    }
//});
