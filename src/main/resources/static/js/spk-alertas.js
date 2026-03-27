/**
 * ============================================
 * SPK ALERTAS - PADRÃO GLOBAL
 * SweetAlert2 + Comportamento Global
 * ============================================
 */

const SpkAlertas = (function () {

    function baseCustomClass(tipo, confirmButtonClass, cancelButtonClass) {
        return {
            popup: 'spk-swal-popup spk-swal-' + tipo,
            title: 'spk-swal-title',
            htmlContainer: 'spk-swal-html',
            confirmButton: (confirmButtonClass || 'btn btn-primary') + ' spk-swal-confirm',
            cancelButton: (cancelButtonClass || 'btn btn-outline-secondary') + ' spk-swal-cancel',
            actions: 'spk-swal-actions',
            icon: 'spk-swal-icon'
        };
    }

    function confirmarExclusao(callback, titulo, mensagem) {
        Swal.fire({
            title: titulo || 'Confirmar exclusão?',
            text: mensagem || 'Esta ação não poderá ser desfeita.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '<i class="fas fa-trash-alt mr-1"></i>Sim, excluir',
            cancelButtonText: '<i class="fas fa-times mr-1"></i>Cancelar',
            reverseButtons: true,
            focusCancel: true,
            allowOutsideClick: false,
            allowEscapeKey: true,
            customClass: baseCustomClass('warning', 'btn btn-danger', 'btn btn-outline-secondary'),
            buttonsStyling: false
        }).then((result) => {
            if (result.isConfirmed && typeof callback === 'function') {
                callback();
            }
        });
    }

    function sucesso(titulo, mensagem) {
        Swal.fire({
            icon: 'success',
            title: titulo || 'Sucesso!',
            text: mensagem || '',
            confirmButtonText: 'OK',
            customClass: baseCustomClass('success', 'btn btn-success'),
            buttonsStyling: false
        });
    }

    function erro(titulo, mensagem) {
        Swal.fire({
            icon: 'error',
            title: titulo || 'Erro!',
            text: mensagem || '',
            confirmButtonText: 'OK',
            customClass: baseCustomClass('error', 'btn btn-danger'),
            buttonsStyling: false
        });
    }

    function aviso(titulo, mensagem) {
        Swal.fire({
            icon: 'warning',
            title: titulo || 'Atenção!',
            text: mensagem || '',
            confirmButtonText: 'OK',
            customClass: baseCustomClass('warning', 'btn btn-warning'),
            buttonsStyling: false
        });
    }

    function info(titulo, mensagem) {
        Swal.fire({
            icon: 'info',
            title: titulo || 'Informação',
            text: mensagem || '',
            confirmButtonText: 'OK',
            customClass: baseCustomClass('info', 'btn btn-primary'),
            buttonsStyling: false
        });
    }

    function aguarde(mensagem) {
        Swal.fire({
            title: mensagem || 'Aguarde...',
            allowOutsideClick: false,
            allowEscapeKey: false,
            customClass: {
                popup: 'spk-swal-loader',
                title: 'spk-swal-title',
                htmlContainer: 'spk-swal-html',
                icon: 'spk-swal-icon'
            },
            didOpen: () => {
                Swal.showLoading();
            }
        });
    }

    function fechar() {
        Swal.close();
    }

    return {
        confirmarExclusao,
        sucesso,
        erro,
        aviso,
        info,
        aguarde,
        fechar
    };

})();

/**
 * ============================================
 * BIND GLOBAL AUTOMÁTICO
 * ============================================
 */
document.addEventListener('click', function (e) {

    const botaoExcluir = e.target.closest('.spk-btn-excluir, .btn-excluir-usuario, [data-spk-confirm="delete"]');

    if (botaoExcluir) {
        e.preventDefault();
        e.stopPropagation();

        const form = botaoExcluir.closest('form');
        if (!form) return;

        const titulo = botaoExcluir.dataset.title;
        const mensagem = botaoExcluir.dataset.message;

        SpkAlertas.confirmarExclusao(() => form.submit(), titulo, mensagem);
        return;
    }

    const botaoConfirm = e.target.closest('[data-spk-confirm="generic"]');

    if (botaoConfirm) {
        e.preventDefault();

        const titulo = botaoConfirm.dataset.title || 'Confirmar ação?';
        const mensagem = botaoConfirm.dataset.message || 'Deseja continuar?';

        SpkAlertas.confirmarExclusao(() => {
            if (botaoConfirm.href) {
                window.location.href = botaoConfirm.href;
            }
        }, titulo, mensagem);
    }
});

/**
 * ============================================
 * REBIND PARA DATATABLES
 * ============================================
 */
if (window.jQuery && $.fn.DataTable) {
    $(document).on('draw.dt', function () {
        // Delegação global já cobre os botões renderizados dinamicamente
    });
}