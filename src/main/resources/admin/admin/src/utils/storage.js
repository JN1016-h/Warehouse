const storage = {
    set(key, value) {
        localStorage.setItem(key, JSON.stringify(value));
    },
    get(key) {
        const raw = localStorage.getItem(key);
        if (raw == null || raw === '') {
            return '';
        }
        try {
            const parsed = JSON.parse(raw);
            if (parsed == null) {
                return '';
            }
            return typeof parsed === 'string' || typeof parsed === 'number' || typeof parsed === 'boolean'
                ? String(parsed)
                : raw;
        } catch (e) {
            // 兼容历史非 JSON 写入
            return String(raw).replace(/^"|"$/g, '');
        }
    },
    getObj(key) {
        const raw = localStorage.getItem(key);
        if (raw == null || raw === '') {
            return null;
        }
        try {
            return JSON.parse(raw);
        } catch (e) {
            return null;
        }
    },
    remove(key) {
        localStorage.removeItem(key);
    },
    clear() {
		localStorage.clear();
    }
}
export default storage;
