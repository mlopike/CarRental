/* =============================================================
   Car Rental System — клиентский JS
   ============================================================= */

/**
 * Открыть/закрыть диалог.
 */
function openDialog(id) {
    document.getElementById(id).classList.add('show');
}

function closeDialog(id) {
    document.getElementById(id).classList.remove('show');
}

// Закрыть диалог по клику на оверлей
document.addEventListener('click', e => {
    if (e.target.classList.contains('dialog-overlay')) {
        e.target.classList.remove('show');
    }
});

/**
 * Расчёт итоговой стоимости в форме заказа.
 */
function calcOrderTotal() {
    const fromEl = document.getElementById('rentFrom');
    const toEl   = document.getElementById('rentTo');
    const priceEl = document.getElementById('pricePerDay');
    const totalEl = document.getElementById('totalPrice');
    const daysEl  = document.getElementById('daysCount');
    if (!fromEl || !toEl || !priceEl || !totalEl) return;

    const from = new Date(fromEl.value);
    const to   = new Date(toEl.value);
    if (isNaN(from) || isNaN(to) || to <= from) {
        totalEl.textContent = '0.00 BYN';
        if (daysEl) daysEl.textContent = '0';
        return;
    }
    const days = Math.round((to - from) / 86400000);
    const price = parseFloat(priceEl.value || '0');
    const total = (price * days).toFixed(2);
    totalEl.textContent = total + ' BYN';
    if (daysEl) daysEl.textContent = days;
}

/**
 * Установить минимальную дату «с» = сегодня, «по» = завтра.
 */
function initOrderDates() {
    const fromEl = document.getElementById('rentFrom');
    const toEl   = document.getElementById('rentTo');
    if (!fromEl || !toEl) return;

    const today = new Date();
    const tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);
    const dayAfter = new Date();
    dayAfter.setDate(today.getDate() + 2);

    const fmt = d => d.toISOString().substring(0, 10);
    fromEl.min = fmt(today);
    toEl.min = fmt(tomorrow);

    if (!fromEl.value) fromEl.value = fmt(tomorrow);
    if (!toEl.value)   toEl.value   = fmt(dayAfter);

    fromEl.addEventListener('change', () => {
        const from = new Date(fromEl.value);
        const next = new Date(from);
        next.setDate(from.getDate() + 1);
        toEl.min = fmt(next);
        if (new Date(toEl.value) <= from) toEl.value = fmt(next);
        calcOrderTotal();
    });
    toEl.addEventListener('change', calcOrderTotal);
    calcOrderTotal();
}

/**
 * Маска для номера банковской карты: 4-4-4-4
 */
function initCardMask() {
    const card = document.getElementById('cardNumber');
    if (!card) return;
    card.addEventListener('input', e => {
        let v = e.target.value.replace(/\D/g, '').substring(0, 16);
        e.target.value = v.replace(/(.{4})/g, '$1 ').trim();
    });
}

document.addEventListener('DOMContentLoaded', () => {
    initOrderDates();
    initCardMask();
});

/**
 * Открыть диалог отклонения с подстановкой ID заказа.
 */
function openRejectDialog(orderId) {
    document.getElementById('rejectOrderId').value = orderId;
    openDialog('rejectDialog');
}

/**
 * Открыть диалог возврата (обычный).
 */
function openReturnOk(orderId) {
    if (!confirm('Зарегистрировать возврат автомобиля без повреждений?')) return;
    document.getElementById('returnOkOrderId').value = orderId;
    document.getElementById('returnOkForm').submit();
}

/**
 * Открыть диалог возврата с повреждениями.
 */
function openDamageDialog(orderId) {
    document.getElementById('damageOrderId').value = orderId;
    openDialog('damageDialog');
}
