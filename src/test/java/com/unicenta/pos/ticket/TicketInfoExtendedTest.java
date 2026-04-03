package com.unicenta.pos.ticket;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class TicketInfoExtendedTest {

    private static final TaxInfo BTW = new TaxInfo("1", "BTW", "001", null, null, 0.21, false, 1);

    private static TicketLineInfo line(String id, String name, double price) {
        return new TicketLineInfo(id, name, "001", "", 1.0, price, BTW);
    }

    // --- new ticket has 0 lines ---

    @Test
    public void newTicketHasZeroLines() {
        TicketInfo ticket = new TicketInfo();
        assertEquals(0, ticket.getLinesCount());
    }

    // --- setLines + getLinesCount ---

    @Test
    public void setLinesThenGetLinesCount() {
        TicketInfo ticket = new TicketInfo();
        List<TicketLineInfo> lines = new ArrayList<>();
        lines.add(line("p1", "Product A", 2.50));
        lines.add(line("p2", "Product B", 1.00));
        ticket.setLines(lines);
        assertEquals(2, ticket.getLinesCount());
    }

    // --- getLine by index ---

    @Test
    public void getLineByIndexReturnsCorrectLine() {
        TicketInfo ticket = new TicketInfo();
        List<TicketLineInfo> lines = new ArrayList<>();
        TicketLineInfo first = line("p1", "Product A", 2.50);
        TicketLineInfo second = line("p2", "Product B", 1.00);
        lines.add(first);
        lines.add(second);
        ticket.setLines(lines);
        assertSame(first, ticket.getLine(0));
        assertSame(second, ticket.getLine(1));
    }

    // --- insertLine ---

    @Test
    public void insertLineIncreasesCount() {
        TicketInfo ticket = new TicketInfo();
        List<TicketLineInfo> lines = new ArrayList<>();
        lines.add(line("p1", "Product A", 2.50));
        lines.add(line("p2", "Product B", 1.00));
        ticket.setLines(lines);

        TicketLineInfo inserted = line("p3", "Product C", 3.00);
        ticket.insertLine(1, inserted);

        assertEquals(3, ticket.getLinesCount());
        assertSame(inserted, ticket.getLine(1));
    }

    @Test
    public void insertLineAtStartShiftsOthers() {
        TicketInfo ticket = new TicketInfo();
        List<TicketLineInfo> lines = new ArrayList<>();
        TicketLineInfo original = line("p1", "Product A", 2.50);
        lines.add(original);
        ticket.setLines(lines);

        TicketLineInfo inserted = line("p0", "Product Z", 9.99);
        ticket.insertLine(0, inserted);

        assertSame(inserted, ticket.getLine(0));
        assertSame(original, ticket.getLine(1));
    }

    // --- removeLine ---

    @Test
    public void removeLineDecreasesCount() {
        TicketInfo ticket = new TicketInfo();
        List<TicketLineInfo> lines = new ArrayList<>();
        lines.add(line("p1", "Product A", 2.50));
        lines.add(line("p2", "Product B", 1.00));
        ticket.setLines(lines);

        ticket.removeLine(0);

        assertEquals(1, ticket.getLinesCount());
    }

    @Test
    public void removeLineShiftsRemainingLines() {
        TicketInfo ticket = new TicketInfo();
        List<TicketLineInfo> lines = new ArrayList<>();
        TicketLineInfo first = line("p1", "Product A", 2.50);
        TicketLineInfo second = line("p2", "Product B", 1.00);
        lines.add(first);
        lines.add(second);
        ticket.setLines(lines);

        ticket.removeLine(0);

        assertSame(second, ticket.getLine(0));
    }

    // --- getLines returns list ---

    @Test
    public void getLinesReturnsSetList() {
        TicketInfo ticket = new TicketInfo();
        List<TicketLineInfo> lines = new ArrayList<>();
        lines.add(line("p1", "Product A", 2.50));
        ticket.setLines(lines);

        List<TicketLineInfo> result = ticket.getLines();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getLinesOnNewTicketIsEmpty() {
        TicketInfo ticket = new TicketInfo();
        List<TicketLineInfo> result = ticket.getLines();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
