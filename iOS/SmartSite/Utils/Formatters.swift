import Foundation

enum Formatters {
    /// dd/MM/yyyy — the date format used everywhere in the mockup.
    static let frenchDate: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd/MM/yyyy"
        formatter.locale = Locale(identifier: "fr_FR")
        return formatter
    }()

    static func date(_ components: DateComponents) -> Date {
        Calendar.current.date(from: components) ?? Date()
    }
}

extension Date {
    var frenchFormatted: String { Formatters.frenchDate.string(from: self) }
}
