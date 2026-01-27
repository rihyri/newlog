export const truncateText = (text, limit = 20, suffix = '...') => {
    if (!text || typeof text !== 'string') return '';
    if (text.length <= limit) return text;
    return text.substring(0, limit) + suffix;
}